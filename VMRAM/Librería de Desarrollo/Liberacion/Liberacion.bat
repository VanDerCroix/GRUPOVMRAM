echo off

set folder="liberacion_%date:/=-%"
mkdir %folder%

copy VMRAM-FPP.xlsx %folder%\VMRAM-PP.xlsx
cd %folder%

mkdir codigo
mkdir documentacion
mkdir ejecutables
mkdir scripts

