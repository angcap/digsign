#!/bin/bash
echo "verifying $1 to $1.pdf"
openssl smime -decrypt -verify -inform DER -in $1 -noverify -out  "$1.pdf"

