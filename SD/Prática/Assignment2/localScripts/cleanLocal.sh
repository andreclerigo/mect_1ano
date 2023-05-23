echo "Cleaning generated folders localy"
echo "  Removing .zip files"
rm -rf *.zip

echo "  Removing generated folders"
rm -rf ../dirAssaultParty
rm -rf ../dirConcetSite 
rm -rf ../dirControlSite
rm -rf ../dirGeneralRepos
rm -rf ../dirMstThief
rm -rf ../dirMuseum
rm -rf ../dirOrdThief

echo "  Removing generated .class files"
rm ../*/*.class
rm ../*/*/*.class
