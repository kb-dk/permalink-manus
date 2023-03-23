package dk.kb.mets;

import java.util.HashMap;

public class MultilingualTerms {

    HashMap<String,HashMap<String,String>>TermMap =
	new HashMap<String,HashMap<String,String>>{
		"dan"=>new HashMap<String,String>{"GotoPage" => "Gå til",
			"Intro" => "Indledning",
			"OtherManus" => "Andre Manuskripter",
			"StandardImg" => "Standardbillede",
			"e-manuskripter" => "e-manuskripter"},
		    "eng"=>new HashMap<String, String>{
			    "GotoPage" => "Go to",
				"Intro" => "Introduction",
				"OtherManus" => "Other manuscripts",
				"StandardImg" => "Standard image",
				"ImageVersion" => "Facsimile",
				"Page" => "Page",
				"Search" => "Search",
				"SearchWords" => "Search word(s)",
				"TextVersion" => "Transcription",
				"NumSearchHits" => "Hits",
				"NoPagesFound" => "No Pages Found!",
				"e-manuskripter" => "e-manuscripts"
				},
		    "esp" => new HashMap<String, String>{
			    "GotoPage" => "Ir a",
				"Intro" => "Introducción",
				"OtherManus" => "Otros manuscritos",
				"StandardImg" => "Imagen normal",
				"Page" => "Página",
				"Search" => "Buscar",
				"TextVersion" => "Transcripción",
				"ImageVersion" => "Facsímil",
				"SearchWords" => "Término(s) de búsqueda",
				"NumSearchHits" => "Acierto(s)"
				},
		    "ara" => new HashMap<String, String>{
			    "GotoPage" => "إذهب إلى",
				"Intro" => "مقدمة",
				"OtherManus" => "مخطوطات أخرى",
				"StandardImg" => "صورة قياسية"
				},
		    "ger" => new HashMap<String, String>{
			    "Intro" => "Einleitung"
				}
	};

    public MultilingualTerms() {}

}
