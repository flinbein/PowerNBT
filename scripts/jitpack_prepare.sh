if [[ $VERSION =~ ^v[0-9.]*$ ]]
then
  echo "${VERSION:1}" > PLUGIN_VERSION.var
elif [[ $VERSION =~ ^[0-9.]*$ ]]
then
  echo "${VERSION}" > PLUGIN_VERSION.var
elif [[ $VERSION =~ -SNAPSHOT$ ]]
then
  echo "${VERSION}" > PLUGIN_VERSION.var
else
  echo "${VERSION}-SNAPSHOT" > PLUGIN_VERSION.var
fi
echo "save PLUGIN_VERSION.var = `cat ./PLUGIN_VERSION.var`"