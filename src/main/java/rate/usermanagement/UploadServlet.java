package rate.usermanagement;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class UploadServlet extends HttpServlet {
	static public BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Map<String, BlobKey> blobs = blobstore.getUploadedBlobs(request);
        BlobKey blobKey = blobs.get("image");
        response.sendRedirect("/update.jsp?blobkey=" + blobKey.getKeyString());
	}
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	BlobKey blobKey = new BlobKey(request.getParameter("blobkey"));
        blobstore.serve(blobKey, response);
    }
}
