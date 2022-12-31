@ echo off
cls

rem R2QC.bat - Run our simple BAT test and publish test results to Quality Center.

rem Location of test results.

set RESULT_PATH=test-output\junitreports

rem Location of Results2QC.pl

set R2QC_PATH=src\test\resources\tools\R2QC
set R2QC=%R2QC_PATH%\R2QC_LSR.pl
set R2QC_RES=%R2QC_PATH%\Stylesheets\prj

rem Set general parameters for Result2QC.pl

set PRODUCT=BAT
set MODULE=%PRODUCT%
set KEYWORDS=Selenium
set STYLESHEET=%R2QC_RES%\Lifesaver_manual.xsl
set QC_PROXY=http://g6w2381g.atlanta.hp.com/qcproxy/wsqccommon.asmx
set TEST_GROUP=
set UPDATE_STEPS=-updateSteps true

rem Delete any previous run.log.

del /Q run.log

rem Determine the current build from TortoiseSVN (use if running on localhost rather than DIS).

set SVN_LOGFILE=%APPDATA%\TortoiseSVN\logfile.txt

find "At revision" "%SVN_LOGFILE%" > currentRevision.tmp
for /F "tokens=5" %%X in (currentRevision.tmp) do set BUILD=r%%X
del /Q currentRevision.tmp

rem Set test run-specific parameters.

set RELEASE=R4
set ITERATION=03

rem Set test suite-specific parameters for the tests.

set SUITE=%PRODUCT%
set CONFIG=localhost
set BUILD=1414

set TESTPLAN_ROOT=Subject\%PRODUCT%
set TESTLAB_ROOT=Root\%PRODUCT%\DTC_BAT

set TEST_ROOT=test.cases.BAT
set RESULT_FILES=%RESULT_PATH%\TEST-%TEST_ROOT%.*.xml

echo .
echo == Publish the LIFESAVER test results to Quality Center ==
echo .
echo on
perl "%R2QC%" -suite "%SUITE%" -config "%CONFIG%" -product "%PRODUCT%" -release "%RELEASE%" -build "%BUILD%" -qcPlanSubject "%TESTPLAN_ROOT%" -qcLabRoot "%TESTLAB_ROOT%" -testRoot "%TEST_ROOT%" -testGroup "%TEST_GROUP%" -keywords "%KEYWORDS%" -module "%MODULE%" -stylesheet "%STYLESHEET%" -httpProxy "%HTTP_PROXY%" -qcProxy "%QC_PROXY%" %UPDATE_STEPS% -resultFiles "%RESULT_FILES%"

echo .
echo === Publishing test results complete! ===
