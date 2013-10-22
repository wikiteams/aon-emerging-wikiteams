package strategies;

/***
 * 
 * Strategy for Agent {strategy for choosing tasks}
 * 
 * @author Oskar Jarczyk
 * @since 1.0
 * @version 1.1
 *
 */
public class Strategy {
	
	public TaskChoice taskChoice;
	public SkillChoice skillChoice;
	
	public enum TaskChoice {
		/**
		 * Homofilia - milosc do tego samego - szukamy takiego taska maksymalnie 
		 * podobnego nad ktorym ju¿ pracowalismy. Jezeli jeszcze nie pracowalismy
		 * nad zadnym to szukamy pierwszego mozliwie odpowiadajacego naszej
		 * macierzy umiejetnosci. Wtedy budowanie doswiadczenia bedzie w miare
		 * odpowiadalo elementom w naszej macierzy. I agent caly czas stara sie
		 * szukac podobnych taskow.
		 */
		HOMOPHYLY,
		/**
		 * Tutaj przeciwienstwo tego co wyzej, agent stara sie znalezc zupelnie
		 * inne taski niz te nad ktorym dotychczas pracowal. Jest to strategia
		 * tworzaca doswiadczenie. Jezeli agent nie pracowal jeszcze nad zadnym taskiem,
		 * to szuka takiego taska ktory zupelnie nie odpowiada macierzy jego
		 * umiejetnosci.
		 */
		HETEROPHYLY,
		/**
		 * Social vector - znajdowanie mozliwie najblizszego wektora umiejetnosci.
		 * Umiejetnosci niesie ze soba dodatkowe informacje - kategoria umiejetnosci
		 * - np. programowanie niskopoziomowe, frontend, itp. Wiec mimo ze nie posiadamy
		 * takich umiejetnosci jakie maja taski w puli, to szukamy najblizej pasujacego
		 * wektora i bierzemy ten task
		 */
		SOCIAL_VECTOR,
		/**
		 * Losowanie taska z puli dostepnych - tylko pod warunkiem ze ma choc jeden skill
		 * nad ktorym jest mozliwe pracowanie. Generalnie stosujemy Random, ale mozna 
		 * rowniez uruchomic jakis rozklad normalny.
		 */
		RANDOM,
		/**
		 * Ta strategia polega na porownywaniu siebie do innych uzytownikow, agent
		 * stara sie nasladowac innego mozliwie podobnego do siebie uzytkownika
		 * sprawdza gdzie pracuje, i pracuje nad tym samym.
		 */
		COMPARISION,
		/**
		 * To wymaga podlaczenia sie do jakiegos datasetu uczacego sie, narazie
		 * low priority. ?
		 */
		MACHINE_LEARNED
	}
	
	public enum SkillChoice {
	    PROPORTIONAL_TIME_DIVISION,
	    GREEDY_ASSIGNMENT_BY_TASK,
	    CHOICE_OF_AGENT,
	    RANDOM
	}
	
	@Override
	public String toString(){
		return this.taskChoice.name() + "," + this.skillChoice.name();
	}

}
