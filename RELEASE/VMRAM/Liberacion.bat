@echo off
set folder="liberacion_%date:/=-%"
mkdir %folder%

copy create_excel_file.ps1 %folder%\create_excel_file.ps1
Powershell.exe -executionpolicy remotesigned -File %folder%\create_excel_file.ps1

cd %folder%
mkdir codigo
mkdir documentacion
mkdir ejecutables
mkdir scripts
