Osztott rendszerek beadandó feladat
Valósítsd meg az alábbi játékot, amelyben világutazókat ábrázolunk.

<h1>WorldMain</h1>
A játék fő osztálya a WorldMain, ez induláskor elindít egy szervert a 4321 porton. A szerver egyszerre több klienst is ki tud szolgálni. A szerverrel szövegesen lehet kommunikálni, és a következő utasításokat lehet kiadni egy-egy sorban:

city <név>: a megadott nevű város létrejön, mint célpont
a rendszer a 35000..35010 intervallumból véletlenszerűen választ számára egy portot
ha a port már foglalt, újra próbálkozik
a szerver válasza egy sor, a port értéke
ha minden port foglalt az intervallumból, akkor a válasz sor tartalma failed, és nem jön létre a város
a várost kiszolgáló portot külön szál kezeli, ez rögtön el is indul, működését lásd lent
cityinfo <név>: a szerver elküldi a megadott nevű város portját (vagy a none üzenetet, ha nincsen ilyen város)
citylist: a szerver elküldi egy-egy sorban a létező városok nevét, majd egy üres sort
person <név>: létrejön egy új utazó a megadott névvel
ha a rendszerben még nincsen város, a szerver visszaküldi a failed üzenetet, és nem jön létre az utazó
a rendszer kiválasztja a 36000..37000 intervallumból a legkisebb, még nem foglalt portot neki
az utazó sorszáma a port “alsó fele”, pl. 3, ha a 36003 portra került az utazó
a szerver válasza egy sorban: <név> <sorszám>, pl. Joe 3, ha Joe a 36003 portra került
ha a név nincsen megadva, akkor a persons.txt fájl sorai közül véletlenszerűen választ egy nevet neki
az utazót kiszolgáló portot külön szál kezeli, ez rögtön el is indul, működését lásd lent
do <név vagy port vagy sorszám> <akció>: az utazó a megadott akciót teszi meg
az első paraméter lehet Joe vagy 3 vagy 36003 a fenti utazó esetén
ha a név nem egyértelmű, a legkisebb sorszámúra vonatkozik
a program megpróbál kapcsolatba lépni az utazó portjával
ha sikertelen, a szerver válasza a kérésre egy sorban failed
ha sikeres, elküldi az akció nevét, majd bontja a kapcsolatot az utazóval, és done üzenetet küld vissza a kliensnek
finished <név>: az utazó így jelzi, hogy befejezte a működését
az exit üzenetben leírtak megvalósítása részben ide kerül majd
exit: a szerver leállít minden várost és utazót (kapcsolódik a portjaikra és elküldi nekik az exit üzenetet), majd kilép
először az utazókat állítja le, majd megvárja mindegyik utazótól a finished üzenetet, és utána állítja le a városokat
a városok leállítása előtt lekéri az info adatokat a városról, és kiírja a városokat kétszer a sztenderd kimenetre:
először a Best city to take a selfie üzenet után sorolja fel őket a városban készült szelfik darabszáma szerinti csökkenő sorrendben (egyenlőség esetén ábécésorrendben), a darabszámokat is kiírva
másodszor a Best city to spend money in üzenet után sorolja fel őket, ekkor az összköltés szerinti sorrendben
amikor az exit működés megkezdődött, a city és person üzenetekre failed választ küld a szerver
Automatizált működés
Ha a WorldMain osztály kap egy parancssori paramétert is, akkor a fenti szerver elindítása mellett a következő tevékenységet is végzi.

Készít 3..5 várost (a neveiket véletlenszerűen választja a cities.txt fájlból).
Készít 4..8 utazót.
A paraméterben (ami egy szám) megadott másodperc múlva elküldi az exit üzenetet a szervernek. Ennek hatására a programnak rövid időn belül ki kell lépnie.

<h1>City</h1>
A városok létrehoznak egy city-<városnév>-<port>.txt naplófájlt, pl. city-Budapest-35008.txt.
A városok szekvenciálisan fogadják a kliensek kapcsolódását a portjukon, és a következő utasításokat lehet kiadni a számukra egy-egy sorban:
  
arrive <név>: a naplóba bekerül az <idő>: <név> arrived bejegyzés
leave <név>: a naplóba bekerül az <idő>: <név> left bejegyzés
spend <név> <összeg>: a naplóba bekerül az <idő>: <név> spent <összeg> bejegyzés
selfie <név>: a naplóba bekerül az <idő>: <név> took a selfie bejegyzés
info: a szerver egy-egy sorban elküldi, mennyi pénzt költöttek a városban összesen, és hány szelfit készítettek
exit: a város szervere leáll, szála kilép
a naplóba még belekerül a people have spent <összeg> in total üzenet, ahol az összeg a látogatók teljes költése
A fentiekben az idő a város szálának elindulása óta eltelt idő ezredmásodpercben.

<h1>Person</h1>
Az utazó, amikor elindul, véletlenszerűen választ egy várost a lehetségesek közül, és belép oda (elküldi az arrive üzenetet).
Az utazók a következő üzeneteket fogadják, ezek a lehetséges akcióik.

go: az utazó elmegy egy véletlenszerű városba
a régi városnak leave, az új városnak arrive üzenetet küld
go <név> alakban is hívható az akció (ha a felhasználó kézzel kapcsolódik a portra), ekkor a megadott nevű városba megy az utazó
spend és selfie: a város szerverének a megfelelő üzenetet küldi (spend esetén 10..1000 közötti véletlen számmal)
exit: az utazó szervere leáll, szála kilép
a szervernek elküldi a finished üzenetet
Az utazók önállóan is cselekszenek: 2..5 másodperc elteltével megtesznek egy véletlenszerűen kiválasztott akciót (az exit kivételével). Amikor az exit üzenetet megkapják, még megtehetik az utolsó akciójukat, aztán kilépnek.
