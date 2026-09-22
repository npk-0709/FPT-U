Add-Type -AssemblyName System.IO.Compression.FileSystem

# Extract the requirements PDF content using different approach
# Since we can't read PDF directly, let's check the evaluation template
$evalPath = 'c:\Users\Khuong\Desktop\FPTU\LAB211\03_J1.L.P0037.EmployeePayrollManagementSystem_450LOCs\Report_EmployeePayrollManagementSystem.docx'
$outputPath = 'c:\Users\Khuong\Desktop\FPTU\LAB211\03_J1.L.P0037.EmployeePayrollManagementSystem_450LOCs\report_template_text.txt'

$zip = [System.IO.Compression.ZipFile]::OpenRead($evalPath)
$entry = $zip.Entries | Where-Object { $_.FullName -eq 'word/document.xml' }
$stream = $entry.Open()
$reader = New-Object System.IO.StreamReader($stream)
$xml = $reader.ReadToEnd()
$reader.Close()
$stream.Close()
$zip.Dispose()

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
Write-Output "Extracted. Total paragraphs: $($paragraphs.Count)"
