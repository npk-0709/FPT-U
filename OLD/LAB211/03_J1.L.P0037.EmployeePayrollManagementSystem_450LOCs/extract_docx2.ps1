Add-Type -AssemblyName System.IO.Compression.FileSystem
$docxPath = 'c:\Users\Khuong\Desktop\FPTU\LAB211\03_J1.L.P0037.EmployeePayrollManagementSystem_450LOCs\Lab211_Su26_EmployeePayroll__SE203056_NguyenPhuKhuong_updated\Report_EmployeePayrollManagementSystem.docx'
$outputPath = 'c:\Users\Khuong\Desktop\FPTU\LAB211\03_J1.L.P0037.EmployeePayrollManagementSystem_450LOCs\report_text.txt'

$zip = [System.IO.Compression.ZipFile]::OpenRead($docxPath)
$entry = $zip.Entries | Where-Object { $_.FullName -eq 'word/document.xml' }
$stream = $entry.Open()
$reader = New-Object System.IO.StreamReader($stream)
$xml = $reader.ReadToEnd()
$reader.Close()
$stream.Close()
$zip.Dispose()

# Parse XML and extract text
[xml]$doc = $xml
$nsmgr = New-Object System.Xml.XmlNamespaceManager($doc.NameTable)
$nsmgr.AddNamespace('w', 'http://schemas.openxmlformats.org/wordprocessingml/2006/main')

$paragraphs = $doc.SelectNodes('//w:p', $nsmgr)
$output = @()
foreach ($p in $paragraphs) {
    $texts = $p.SelectNodes('.//w:t', $nsmgr)
    $line = ($texts | ForEach-Object { $_.'#text' }) -join ''
    $output += $line
}

$output -join "`n" | Out-File -FilePath $outputPath -Encoding UTF8
Write-Output "Extracted text saved to $outputPath"
Write-Output "Total paragraphs: $($paragraphs.Count)"
