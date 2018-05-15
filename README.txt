================================== Proiect IDP ================================

1. Descriere Proiect:
	O aplicatie Android in care utilizatorii se logheaza cu profilul de 
	Facebook si pot vizualiza pozitia lor geografica si distanta fata de
	prietenii lor.

2. Detalii implementare proiect:
	Client:
		Aplicatia contine doua activitati: activitatea de Login si activitatea
		principala in care sunt afisate informatiile.

		In ecranul de login ii este prezentat utilizatorului butonul de
		autentificare cu Facebook. Am folosit Facebook SDK pentru a implementa
		procesul de logare. Odata ce utilizatorul s-a logat, retin id-ul,
		numele si prenumele pe care le transmit mai departe la activitatea
		principala. De asemenea, se lanseaza un request catre sericiul din
		background care trimite catre server (sub forma de JSON) id-ul, numele
		si prenumele pentru a fi adaugat in baza de date, in cazul in care
		acesta nu exista.

		Activitatea principala consta intr-o zona de text, in partea de sus
		a ecranului in care este afisata pozitia geografica a utilizatorului.
		In partea de jos este un RecyclerView in care sunt afisati prietenii
		utilizatorului impreuna cu distanta fata de acestia si timpul scurs
		de la ultimul update. Am ales sa folosesc RecyclerView deoarece, daca
		vor fi multi prieteni de afisat, acest tip de View eficientizeaza
		crearea obiectelor prin refolosirea TextView-urilor.
		La pornirea activitatii se instantiaza serviciul de localizare si
		se verifica permisiunile pentru accesul la locatie.
		Pentru a primi update-uri de la serviciul de localizare, am implementat
		un LocationListener, care, atunci cand apare un update de locatie,
		updateaza TextView-ul din partea de sus si trimite serviciului din
		background informatiile urmatoare: id-ul utilizatorului, longitudinea,
		latitudinea si altitudinea.
		Serviciul de background (BackendService) primeste datele de la
		activitate (sub forma de Intent) si formeaza un JSON cu acestea. Acest
		JSON este trimis catre server si se asteapta raspunsul de la acesta,
		care va contine informatiile despre distanta si timp pentru toti
		prietenii. Dupa ce a primit informatiile de la server, serviciul
		trimite un broadcast cu lista de prieteni si informatiile asociate.
		Acest broadcast este receptionat de activitate si updateaza informatiile
		din RecyclerView cu ajutorul unui Adapter.

	Server:
		Tehnologia folosita: Java Servlets + Apache Tomcat
			Avantaje:
				- Java ca si tehnologie (OOP, independenta de platforma, 
				garbage collection)
				- robust si clar

		Serverul contine doua servlet-uri: unul pentru adaugarea utilizatorilor
		in baza de date (InsertUserServlet) si unul pentru updatarea pozitiei
		geografice (UpdateLocationServlet).

		Toate datele pe care le primeste serverul sunt sub forma de JSON.

		Structura baza de date:
			- o singura tabela: users, care contine urmatoarele coloane:
				- ID
				- facebook_id
				- first_name
				- last_name
				- longitude
				- latitude
				- altitude
				- last_timestamp

		InsertUserServlet: parseaza JSON-ul primit -> verifica daca utilizatorul
		exista deja in baza de date -> daca nu exista, il introduce cu datele
		primite.

		UpdateLocationServlet: parseaza JSON-ul primit -> updateaza in baza de
		date pozitia utilizatorului -> calculeaza distanta fata de toti
		prietenii utilizatorului -> calculeaza diferenta de timp fata de
		utimul timestamp al prietenilor utilizatorului -> trimite aceste date
		inapoi catre aplicatie sub forma de JSON.

4. Github:
	- Link: https://github.com/george-cristian/ProiectIDP

3. Student:
	- Nume: Cristian George
	- Grupa: 341C5
	- Email: george.crist5@gmail.com