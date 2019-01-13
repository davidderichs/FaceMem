<h1>Backlog</h1>

<ol>
	<li>
		<p>Basic Layout</p>
		<p><i>TabLayout mit drei Tabs als Navigation.
        Alle Views sind bereits teil der Activity und werden je nach Kontext ein- bzw. 	  
        ausgeblendet (bessere Performance). </i> </p>
	</li>	
	<li>
		<p>Datensatz-Darstellung als Grid</p>
        <p> <i> GridViewLayout erzeugt und in xml ausgelagert. Grid-Elemente bestehen 
        aus  TextView  und ImageView. </i></p> 
	</li>	
	<li>
        <p>Neue Personen hinzufügen (GUI)</p>
        <p>
            <i>Name kann angegeben werden. Foto kann noch nicht hinzugefügt werden. Button zum Speichern hinzugefügt.</i>            
        </p>
	</li>	
	<li>
        <p>Camera einbinden</p>
        <p>
            <i>Kamera funktionalität eingebunden. (zur Zeit nur Thumbnail). Button zur Foto-Aufnahme eingefügt.</i>
        </p>
	</li>	
	<li>
		<p>ImagePicker einbinden</p>
        <p>
            <i>Zugriff auf Galerie, um lokale Fotos einzubinden. Button zur Bild-Auswahl hinzugefügt.</i>
        </p>
	</li>
    <li>
    	<p>Eingabeformular für neue Datensätze fertig eingerichtet</p>
        <p>
            <i>Bild der Person und Namen können angegeben werden. 
            Außerdem kann der User entweder die Kamera nutzen, um ein Bild hinzuzufügen oder aus der Galerie ein Bild hinzufügen. Datenspeicherung leider noch nicht möglich, aber GUI Layout finalisiert..</i>
        </p>        
	</li>
    <li>
    	<p>Laufzeit Datenspeicherung eingerichtet. Speicherung der Personen in Sample-Arrays und verwendung dieser Daten von den GUI-Elementen. </p>
	</li>
    <li>
    	<p>Datenspeicherung mit SQLite eingerichtet.</p>
        <p>
            <i>FeedEntry, FeedReaderContract und FeedReaderDbHelper für die Anbindung genutzt. Tabelle beinhaltet id, Name und Pfad zur Bild-Datei. GUI-Elemente und Controller verwenden nun Daten aus der Datenbank und nicht mehr nur aus Laufzeit-Variablen, die bei Start mit sample Daten gefüllt werden.</i>
        </p>
	</li>
    <li>
    	<p>Lern-Funktion eingebaut.</p>
        <p>
            <i>Daten aus der Datenbank werden gecached und in Arrays gespeichert. Per Zufall wird ein Bild angezeigt und ein beliebier Name. Der User muss nun entscheiden, ob Bild und Name zusammengehören oder nicht. Die App informiert ihn, ob seine Annahme richtig war oder nicht und ermögicht das laden einer neuen Frage.</i>
        </p>
	</li>
    <li>
    	<p>Test auf Nativem Gerät</p>
        <p>
            <i>App auf Xperia Z Ultra erfolgreich getestet. Keine Fehler beim erstellen neuer Datensätze und auch im Lernmodus sind keine Fehler aufgetreten. Die Darstellung aller Personen als Grid ist etwas zu klein, was der Display-Größe zugrunde liegt.</i>
        </p>
	</li>
</ol>








