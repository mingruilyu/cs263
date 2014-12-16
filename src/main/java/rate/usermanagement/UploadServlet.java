package rate.usermanagement;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
/**
 * This class handles the image upload operation.
 * @author Mingrui Lyu
 * @version 1.0
 */
public class UploadServlet extends HttpServlet {
	/**
	 * This static field is the blobstore service of GAE
	 */
	static public BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	/**
	 * This method is a REST POST API. It stores the image in the blobstore and
	 * redirect to a new url that has the blobkey of the image as a parameter.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, BlobKey> blobs = blobstore.getUploadedBlobs(request);
        BlobKey blobKey = blobs.get("image");
        response.sendRedirect(request.getHeader("Referer") + "?blobkey=" + blobKey.getKeyString());
	}
	/**
	 * This method is a REST GET API. It uses blobkey parameter to retrieve the image
	 * in the blobstore and server the request with the image.
	 */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	BlobKey blobKey = new BlobKey(request.getParameter("blobkey"));
        blobstore.serve(blobKey, response);
    }
}
