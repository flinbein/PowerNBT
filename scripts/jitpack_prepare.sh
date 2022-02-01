if [[ $VERSION == v* ]]
then
  echo "${VERSION:1}" >> PLUGIN_VERSION.var
else
  echo "${VERSION:1}-SNAPSHOT" >> PLUGIN_VERSION.var
fi
echo "save PLUGIN_VERSION.var = `cat ./PLUGIN_VERSION.var`"