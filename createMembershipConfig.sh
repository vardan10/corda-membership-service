#echo 'bnoWhitelist = ["O=BNO,L=New York,C=US"]
#notaryName = "O=Notary,L=London,C=GB"' > $PWD/workflows-kotlin/build/nodes/BNO/cordapps/config/membership-service.conf
#
#echo 'bnoWhitelist = ["O=BNO,L=New York,C=US"]
#notaryName = "O=Notary,L=London,C=GB"' > $PWD/workflows-kotlin/build/nodes/PartyA/cordapps/config/membership-service.conf
#
#echo 'bnoWhitelist = ["O=BNO,L=New York,C=US"]
#notaryName = "O=Notary,L=London,C=GB"' > $PWD/workflows-kotlin/build/nodes/PartyB/cordapps/config/membership-service.conf



cp $PWD/updates-config/bno/config/* $PWD/workflows-kotlin/build/nodes/BNO/cordapps/config/
cp $PWD/updates-config/member/config/* $PWD/workflows-kotlin/build/nodes/PartyA/cordapps/config/
cp $PWD/updates-config/member/config/* $PWD/workflows-kotlin/build/nodes/PartyB/cordapps/config/

cp $PWD/membership-config/bno/config/* $PWD/workflows-kotlin/build/nodes/BNO/cordapps/config/
cp $PWD/membership-config/member/config/* $PWD/workflows-kotlin/build/nodes/PartyA/cordapps/config/
cp $PWD/membership-config/member/config/* $PWD/workflows-kotlin/build/nodes/PartyB/cordapps/config/

