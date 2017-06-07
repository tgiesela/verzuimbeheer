set _JAVA_OPTIONS=-Duser.home=%userprofile%
if not exist %userprofile%\verzuimbeheer mkdir %userprofile%\verzuimbeheer
java -DAS_DERBY_INSTALL=. -jar mvcgui-1.1.jar