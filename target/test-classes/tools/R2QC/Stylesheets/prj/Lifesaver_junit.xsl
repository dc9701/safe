<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
<testSuite><xsl:for-each select="testsuite/testcase">
	<test>
		<xsl:attribute name="testName"><xsl:value-of select="@name"/></xsl:attribute>
		<xsl:attribute name="testDescription"/>
		<xsl:attribute name="testDuration"><xsl:value-of select="ceiling(@time)"/></xsl:attribute>
		<xsl:attribute name="testAutomation">Automated</xsl:attribute>
		<xsl:attribute name="testProduct">{{product}}</xsl:attribute>
		<xsl:attribute name="testModule">{{module}}</xsl:attribute>
		<xsl:attribute name="testKeywords">{{keywords}}</xsl:attribute>
		<xsl:attribute name="testScriptPath">{{qcPlanSubject}}.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:attribute name="testSetPath">{{qcLabRoot}}.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:attribute name="testSetNotPassedPath">{{qcLabRootNotPassed}}.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:attribute name="testSetName">.<xsl:value-of select="@classname"/>.<xsl:value-of select="@name"/></xsl:attribute>
		<xsl:choose>
			<xsl:when test="error">
                <xsl:choose>
                    <xsl:when test="error/@message='Not yet implemented'">
                        <xsl:attribute name="testExecutionStatus">No Run</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="testExecutionStatus">Failed</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
				<xsl:attribute name="testStatus">Repair</xsl:attribute>
				</xsl:when>
			<xsl:when test="failure">
                <xsl:choose>
                    <xsl:when test="failure/@message='Not yet implemented'">
                        <xsl:attribute name="testExecutionStatus">No Run</xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="testExecutionStatus">Failed</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:attribute name="testStatus">Repair</xsl:attribute>
				</xsl:when>
			<xsl:when test="skipped">
				<xsl:attribute name="testExecutionStatus">No Run</xsl:attribute>
				<xsl:attribute name="testStatus">Repair</xsl:attribute>
				</xsl:when>
			<xsl:otherwise>
				<xsl:attribute name="testExecutionStatus">Passed</xsl:attribute>
				<xsl:attribute name="testStatus">Ready</xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>
		<testRun>
			<xsl:attribute name="runName">{{release}} {{suite}} for {{build}} on {{config}}</xsl:attribute>
			<xsl:attribute name="runBuild">{{build}}</xsl:attribute>
			<xsl:attribute name="runConfig">{{config}}</xsl:attribute>
			<xsl:attribute name="runEnvironment">{{environment}}</xsl:attribute>
			<xsl:attribute name="runDuration"><xsl:value-of select="ceiling(@time)"/></xsl:attribute>
			<xsl:choose>
				<xsl:when test="error">
                    <xsl:choose>
                        <xsl:when test="error/@message='Not yet implemented'">
                            <xsl:attribute name="runStatus">No Run</xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="runStatus">Not Completed</xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
					</xsl:when>
				<xsl:when test="failure">
                    <xsl:choose>
                        <xsl:when test="failure/@message='Not yet implemented'">
                            <xsl:attribute name="runStatus">No Run</xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="runStatus">Failed</xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
					</xsl:when>
				<xsl:when test="skipped">
					<xsl:attribute name="runStatus">No Run</xsl:attribute>
					</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="runStatus">Passed</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
			</testRun>
		<testSteps>
			<step>
				<xsl:attribute name="stepName"><xsl:value-of select="@name"/></xsl:attribute>
				<xsl:choose>
					<xsl:when test="error">
                        <xsl:choose>
                            <xsl:when test="error/@message='Not yet implemented'">
                                <xsl:attribute name="stepStatus">No Run</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="stepStatus">Not Completed</xsl:attribute>ERROR - <xsl:value-of select="error/@message"/>
                            </xsl:otherwise>
                        </xsl:choose>
						</xsl:when>
					<xsl:when test="system-err">
						<xsl:attribute name="stepStatus">Failed</xsl:attribute>FAILED - System error:  <xsl:value-of select="system-err"/>
						</xsl:when>
					<xsl:when test="failure">
                        <xsl:choose>
                            <xsl:when test="failure/@message='Not yet implemented'">
                                <xsl:attribute name="stepStatus">No Run</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="stepStatus">Failed</xsl:attribute>FAILED - Failure message:  <xsl:value-of select="failure/@message"/><xsl:value-of select="failure"/>
                            </xsl:otherwise>
                        </xsl:choose>
						</xsl:when>
					<xsl:when test="skipped">
						<xsl:attribute name="stepStatus">No Run</xsl:attribute>
						</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="stepStatus">Passed</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</step>
			</testSteps>
		</test>
	</xsl:for-each>
</testSuite>
</xsl:template>
</xsl:stylesheet>