package dk.kb.mets;

import java.util.HashMap;

public class MultilingualTerms {

    HashMap<String,HashMap<String,String>>termMap = new HashMap<String,HashMap<String,String>>();
    HashMap<String,String> dan = new HashMap<String,String>();
    HashMap<String,String> eng = new HashMap<String,String>();
    HashMap<String,String> esp = new HashMap<String, String>();
    HashMap<String,String> ara = new HashMap<String, String>();
    HashMap<String,String> ger = new HashMap<String, String>();

    HashMap<String,String> languages = new HashMap<String, String>();

    public MultilingualTerms() {
	dan.put("GotoPage","Gå til");
	dan.put("Intro","Indledning");
	dan.put("OtherManus","Andre Manuskripter");
	dan.put("StandardImg","Standardbillede");
	dan.put("e-manuskripter","e-manuskripter");

	eng.put("GotoPage","Go to");
	eng.put("Intro","Introduction");
	eng.put("OtherManus","Other manuscripts");
	eng.put("StandardImg","Standard image");
	eng.put("ImageVersion","Facsimile");
	eng.put("Page","Page");
	eng.put("Search","Search");
	eng.put("SearchWords","Search word(s)");
	eng.put("TextVersion","Transcription");
	eng.put("NumSearchHits","Hits");
	eng.put("NoPagesFound","No Pages Found!");
	eng.put("e-manuskripter","e-manuscripts");

	esp.put("GotoPage","Ir a");
	esp.put("Intro","Introducción");
	esp.put("OtherManus","Otros manuscritos");
	esp.put("StandardImg","Imagen normal");
	esp.put("Page","Página");
	esp.put("Search","Buscar");
	esp.put("TextVersion","Transcripción");
	esp.put("ImageVersion","Facsímil");
	esp.put("SearchWords","Término(s) de búsqueda");
	esp.put("NumSearchHits","Acierto(s)");

	
	ara.put("GotoPage","إذهب إلى");
	ara.put("Intro","مقدمة");
	ara.put("OtherManus","مخطوطات أخرى");
	ara.put("StandardImg","صورة قياسية");

	ger.put("Intro","Einleitung");

	termMap.put("dan",dan);
	termMap.put("eng",eng);
	termMap.put("esp",esp);
	termMap.put("ara",ara);
	termMap.put("ger",ger);

	languages.put("san","Sanskrit");
	languages.put("dan","Dansk");
	languages.put("eng","English");
	languages.put("heb","Hebrew");
	languages.put("ara","ﻲﺒﺮﻋ");
	languages.put("esp","Español");
	languages.put("chi","Chinese");
	languages.put("jpn","Japanese");
	languages.put("per","Persian");
	languages.put("mon","Mongolsk");
	languages.put("tib","Tibetansk");
	languages.put("lat","Latin");
	languages.put("ota","Osmanisk");
	languages.put("ger","German");
	languages.put("fre","French");
	
    }

    public String getLang(String lang) {
	return languages.get(lang);
    }

    public HashMap<String,String> termMap(String lang) {
	return termMap.get(lang);
    }

}
