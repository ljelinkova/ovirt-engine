UPDATE cluster SET cpu_name = 'Intel Nehalem Family'
WHERE cpu_name = 'Intel Nehalem IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Nehalem Family'
WHERE cpu_name = 'Intel Nehalem IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Westmere Family'
WHERE cpu_name = 'Intel Westmere IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Westmere Family'
WHERE cpu_name = 'Intel Westmere IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel SandyBridge Family'
WHERE cpu_name = 'SandyBridge IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel SandyBridge Family'
WHERE cpu_name = 'Intel SandyBridge IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Haswell-noTSX Family'
WHERE cpu_name = 'Intel Haswell-noTSX IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Haswell-noTSX Family'
WHERE cpu_name = 'Intel Haswell-noTSX IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Haswell Family'
WHERE cpu_name = 'Intel Haswell IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Haswell Family'
WHERE cpu_name = 'Intel Haswell IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Broadwell-noTSX Family'
WHERE cpu_name = 'Intel Broadwell-noTSX IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Broadwell-noTSX Family'
WHERE cpu_name = 'Intel Broadwell-noTSX IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Broadwell Family'
WHERE cpu_name = 'Intel Broadwell IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Broadwell Family'
WHERE cpu_name = 'Intel Broadwell IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Skylake Client Family'
WHERE cpu_name = 'Intel Skylake Client IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Skylake Client Family'
WHERE cpu_name = 'Intel Skylake Client IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Intel Skylake Server Family'
WHERE cpu_name = 'Intel Skylake Server IBRS SSBD Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure Intel Skylake Server Family'
WHERE cpu_name = 'Intel Skylake Server IBRS SSBD MDS Family' AND compatibility_version >= '4.4';

UPDATE cluster SET cpu_name = 'Secure AMD EPYC'
WHERE cpu_name = 'AMD EPYC IBPB SSBD' AND compatibility_version >= '4.4';
