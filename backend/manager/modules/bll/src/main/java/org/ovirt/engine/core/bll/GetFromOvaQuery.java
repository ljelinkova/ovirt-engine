package org.ovirt.engine.core.bll;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.ovirt.engine.core.bll.context.EngineContext;
import org.ovirt.engine.core.bll.exportimport.ExtractOvaCommand;
import org.ovirt.engine.core.common.businessentities.VdsStatic;
import org.ovirt.engine.core.common.businessentities.VmEntityType;
import org.ovirt.engine.core.common.errors.EngineError;
import org.ovirt.engine.core.common.errors.EngineException;
import org.ovirt.engine.core.common.queries.GetVmFromOvaQueryParameters;
import org.ovirt.engine.core.common.utils.ansible.AnsibleCommandBuilder;
import org.ovirt.engine.core.common.utils.ansible.AnsibleConstants;
import org.ovirt.engine.core.common.utils.ansible.AnsibleExecutor;
import org.ovirt.engine.core.common.utils.ansible.AnsibleReturnCode;
import org.ovirt.engine.core.common.utils.ansible.AnsibleReturnValue;
import org.ovirt.engine.core.common.utils.ansible.AnsibleVerbosity;
import org.ovirt.engine.core.dao.VdsStaticDao;

public abstract class GetFromOvaQuery <T, P extends GetVmFromOvaQueryParameters> extends QueriesCommandBase<P> {

    @Inject
    private VdsStaticDao vdsStaticDao;
    @Inject
    private AnsibleExecutor ansibleExecutor;

    public GetFromOvaQuery(P parameters, EngineContext engineContext) {
        super(parameters, engineContext);
    }

    @Override
    protected void executeQueryCommand() {
        String stdout = runAnsibleQueryOvaInfoPlaybook();
        stdout = stdout.trim();
        Object result = getParameters().isListDirectory() ? parseOvfs(stdout) : parseOvf(stdout);
        setReturnValue(result);
        getQueryReturnValue().setSucceeded(result != null);
    }

    private String runAnsibleQueryOvaInfoPlaybook() {
        VdsStatic host = vdsStaticDao.get(getParameters().getVdsId());
        AnsibleCommandBuilder command = new AnsibleCommandBuilder()
                .hosts(host)
                .variable("ovirt_query_ova_path", getParameters().getPath())
                .variable("list_directory", getParameters().isListDirectory() ? "True" : "False")
                .variable("entity_type", getEntityType().name().toLowerCase())
                // /var/log/ovirt-engine/ova/ovirt-query-ova-ansible-{hostname}-{timestamp}.log
                .logFileDirectory(ExtractOvaCommand.IMPORT_OVA_LOG_DIRECTORY)
                .logFilePrefix("ovirt-query-ova-ansible")
                .logFileName(host.getHostName())
                .verboseLevel(AnsibleVerbosity.LEVEL0)
                .stdoutCallback(AnsibleConstants.OVA_QUERY_CALLBACK_PLUGIN)
                .playbook(AnsibleConstants.QUERY_OVA_PLAYBOOK);

        AnsibleReturnValue ansibleReturnValue = ansibleExecutor.runCommand(command);
        boolean succeeded = ansibleReturnValue.getAnsibleReturnCode() == AnsibleReturnCode.OK;
        if (!succeeded) {
            log.error("Failed to query OVA info: {}", ansibleReturnValue.getStderr());
            throw new EngineException(EngineError.GeneralException, "Failed to query OVA info");
        }

        return ansibleReturnValue.getStdout();
    }

    private Map<String, T> parseOvfs(String stdout) {
        if (stdout.startsWith("{")) {
            stdout = stdout.substring(1, stdout.length()-1);
            return Arrays.stream(stdout.split("::"))
                    .map(str -> {
                        return splitToFileNameAndOvf(str);
                    })
                    .map(arr -> new Object[] { parseOvf(arr[1]), arr[0]})
                    .filter(arr -> arr[0] != null)
                    .collect(Collectors.toMap(arr -> (String) arr[1], arr -> (T) arr[0]));
        } else {
            String[] splitted = splitToFileNameAndOvf(stdout);
            T vm = parseOvf(splitted[1]);
            return new HashMap<>(Collections.singletonMap(splitted[0], vm));
        }
    }

    protected abstract T parseOvf(String ovf);
    protected abstract VmEntityType getEntityType();

    private String[] splitToFileNameAndOvf(String str) {
        int delimiter = str.indexOf('=');
        return new String[] { str.substring(0, delimiter), str.substring(delimiter+1) };
    }
}
