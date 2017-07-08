$excel = New-Object -ComObject "Excel.Application"
$wb = $excel.Workbooks.Add()
$ws = $wb.ActiveSheet
$excel.Visible = $False
$wb.SaveAs($PSScriptRoot + "\" +"xltest.xlsx")
$wb.Close()
$excel.Quit()

Remove-Item $MyINvocation.InvocationName