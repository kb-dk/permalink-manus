package dk.kb.dup.metsApi;

import org.dom4j.Document;
import javax.cache.*;
import javax.cache.configuration.*;
import javax.cache.Caching;
import javax.cache.CacheManager;
import javax.cache.Cache;
import javax.cache.expiry.*;
import javax.cache.spi.*;
import static javax.cache.expiry.Duration.ONE_HOUR;
import static javax.cache.expiry.Duration.ONE_MINUTE;
import static javax.cache.expiry.Duration.ZERO;

public class Structure
{

    private static Structure struct    = null;

    Cache<String, String> cache = null;
    //Cache cache = null;

    private Structure() {
	//resolve a cache manager
	CachingProvider cachingProvider = Caching.getCachingProvider();
	CacheManager cacheManager = cachingProvider.getCacheManager();

	//configure the cache
	MutableConfiguration<String, String> config = new MutableConfiguration<>();
        config.setStoreByValue(true)
	    .setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(ONE_MINUTE))
	    .setStatisticsEnabled(true);

	//create the cache
	this.cache = cacheManager.createCache("simpleCache", config);
    }

    public static Structure getInstance() {
	if(struct == null) {
	    struct = new Structure();
	}
	return struct;
    }

    public String mets(String app,String docId) {
	//cache operations
	String key = app + docId;

	String val = this.cache.get(key);
	if(val != null ) {
	    return val;
	} else {
	    val = this.cook(app,docId).asXML();
	    this.cache.put(key, val);
	}
	return val;
    }
    

    private Document cook(String app,String docId) {

	Document metsStruct = null;    

	if(app.equalsIgnoreCase("manus")) {
	    Manus manus = new Manus(docId);
	    metsStruct = manus.getMets();
	} else if(app.equalsIgnoreCase("lum")){
	    Lum lum = new Lum(docId);
	    metsStruct = lum.getMets();
	} else if(app.equalsIgnoreCase("lum-proj")) {
	    Lum lum = new Lum();
	    metsStruct = lum.getProjectMets();
	} else if(app.equalsIgnoreCase("musman")) {
	    MusMan musman = new MusMan(docId);
	    metsStruct = musman.getMets();
	} else if(app.equalsIgnoreCase("musman-proj")) {
	    MusMan musman = new MusMan();
	    metsStruct = musman.getProjectMets();
	} else if(app.equalsIgnoreCase("mus")) {
	    try{
		Integer.parseInt(docId);
		Mus mus = new Mus(docId,"");
		metsStruct = mus.getNodeMets();
	    } catch(Exception e){
		Mus mus = new Mus("",docId);
		metsStruct = mus.getProjectMets();
	    }
	} else if(app.equalsIgnoreCase("musik")) {
	    Musik musik = new Musik();
	    metsStruct = musik.getProjectsMets();
	}

	return metsStruct;

    }
}