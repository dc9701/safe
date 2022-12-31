Dim $filePath = '"' & $CmdLine[1] & '\photo1.jpg"'
Dim $dialogTitle = ""

If WinExists("文件上传") Then
   $dialogTitle = "文件上传"
Else
   $dialogTitle = "File Upload"
EndIf

If $CmdLine[3] = "False" Then
	  WinClose($dialogTitle)
	  Exit
Else

	For $iCount = 2 To Int($CmdLine[2])
		$filePath = $filePath & ' "' & $CmdLine[1] & '\photo' & $iCount & '.jpg"'
    Next
	If $CmdLine[0] > 3 Then
	$filePath = $filePath & ' "' & $CmdLine[4] & '"'
    EndIf
 EndIf

FileWrite($CmdLine[1] &'\Input.txt',$filePath)
WinActivate($dialogTitle)
WinWaitActive($dialogTitle)
ControlSetText($dialogTitle,"","Edit1",$filePath)
WinActivate($dialogTitle)
WinWaitActive($dialogTitle)
ControlClick($dialogTitle,"","Button1")

