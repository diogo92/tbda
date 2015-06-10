package tbda;

import java.util.ArrayList;
import java.util.HashMap;

import org.bson.Document;

public class MyMap extends HashMap<Integer, ArrayList<Document>> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void put(Integer key, Document doc) {
        ArrayList<Document> current = get(key);
        if (current == null) {
            current = new ArrayList<Document>();
            super.put(key, current);
        }
        current.add(doc);
    }
}