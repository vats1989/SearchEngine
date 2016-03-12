package springmvc.web.controller;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.tartarus.snowball.ext.PorterStemmer;
import springmvc.web.model.Database;

@Controller
public class HomeController {
	
    @RequestMapping(value=("/home.html"), method=RequestMethod.GET)
    public String index()
    {
        return "home";
    }

    @RequestMapping(value="/search.html",method=RequestMethod.GET)
    public String searchTerm(@RequestParam String term, ModelMap models) throws IOException	{
    	
    	System.out.println(term);
    	
    	PorterStemmer ps = new PorterStemmer();
    	ps.setCurrent(term.toLowerCase());
    	ps.stem();
    	term = ps.getCurrent();
    	
    	Database db1 = new Database(true);
    	Database db2 = new Database(false);
    	Map<String, Double> tfidf = db1.getTFIDFFromDB(term);
    	
    	Map<String, Double> newRankMap = null;
    	
    	if(tfidf != null)	{
    		
    		newRankMap = new HashMap<String, Double>();
			for (String s : tfidf.keySet()) {
				JSONObject json = db2.getPageRank(s);
				Double newscore = Double.parseDouble(json.get("rank").toString()) +tfidf.get(s);
				newscore = Double.parseDouble(new DecimalFormat("##.###").format(newscore));
				newRankMap.put(json.get("path").toString(), newscore);
			}
			newRankMap = sortByValues(newRankMap);
			System.out.println();
			for(String s : newRankMap.keySet())	
				System.out.println(s +" - "+ newRankMap.get(s));
    	}
    	
    	models.put("result", newRankMap);
    	return "result";
    }
    
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, Double> sortByValues(Map<String, Double> map) {
		List list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		HashMap sortedHashMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedHashMap.put(entry.getKey(), entry.getValue());
		}
		return sortedHashMap;
	}
}