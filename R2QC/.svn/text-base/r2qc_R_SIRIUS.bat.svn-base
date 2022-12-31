@ echo off
cls

rem R2QC.bat - Run our simple BAT test and publish test results to Quality Center.

rem Location of test results.

set RESULT_PATH=.\test-output\junitreports

rem Location of Results2QC.pl

set R2QC_PATH=.\src\test\resources\tools\R2QC
set R2QC=%R2QC_PATH%\Results2QC.pl
set R2QC_RES=%R2QC_PATH%\Stylesheets\prj

rem Set general parameters for Result2QC.pl

set PRODUCT=Naples Premium
set MODULE=FW TEST: fpui
set KEYWORDS=Selenium
set STYLESHEET=%R2QC_RES%\Lifesaver_manual.xsl
set QC_PROXY=http://g6w2381g.atlanta.hp.com/qcproxy/wsqccommon.asmx
set UPDATE_STEPS=-updateSteps true

rem Delete any previous run.log.

del /Q run.log

rem Determine the current build from TortoiseSVN (use if running on localhost rather than DIS).

set SVN_LOGFILE=%APPDATA%\TortoiseSVN\logfile.txt

find "At revision" "%SVN_LOGFILE%" > currentRevision.tmp
for /F "tokens=5" %%X in (currentRevision.tmp) do set BUILD=r%%X
del /Q currentRevision.tmp

rem Set test run-specific parameters.

set RELEASE=1418AR
set ITERATION=300 SI
rem set ITERATION=Stage1_OneDayRegression_3.6.2014

rem Set test suite-specific parameters for Unit tests.

set SUITE=UnitTests
set CONFIG=
set BUILD=1418AR
set TESTPLAN_ROOT=Subject\Firmware\Rumble
set TESTLAB_ROOT=Root\Firmware\2014 Products\%PRODUCT%\%ITERATION%\Rumble
set TEST_ROOT=test.cases.rumble
set TEST_GROUP=SW-FW Interaction Test
set RESULT_FILES=%RESULT_PATH%\*.xml

echo .
echo == Publish the test results to Quality Center ==
echo .
echo on
perl "%R2QC%" -suite "%SUITE%" -config "%CONFIG%" -product "%PRODUCT%" -release "%RELEASE%" -build "%BUILD%" -qcPlanSubject "%TESTPLAN_ROOT%" -qcLabRoot "%TESTLAB_ROOT%" -testRoot "%TEST_ROOT%" -testGroup "%TEST_GROUP%" -keywords "%KEYWORDS%" -module "%MODULE%" -stylesheet "%STYLESHEET%" -qcProxy "%QC_PROXY%" %UPDATE_STEPS% -resultFiles "%RESULT_FILES%"
@echo off

echo .
echo === Publishing test results complete! ===
