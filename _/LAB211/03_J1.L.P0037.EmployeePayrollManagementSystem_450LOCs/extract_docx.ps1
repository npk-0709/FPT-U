$word = New-Object -ComObject Word.Application
$word.Visible = $false
$doc = $word.Documents.Open('c:\Users\Khuong\Desktop\FPTU\LAB211\03_J1.L.P0037.EmployeePayrollManagementSystem_450LOCs\Lab211_Su26_EmployeePayroll__SE203056_NguyenPhuKhuong_updated\Report_EmployeePayrollManagementSystem.docx')
Write-Output $doc.Content.Text
$doc.Close()
$word.Quit()
