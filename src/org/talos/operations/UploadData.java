package org.talos.operations;

import java.util.concurrent.ExecutionException;

import org.talos.db.DataDbOperations;
import org.talos.services.WebServiceTask;

import android.content.Context;
import android.widget.Toast;

public class UploadData {
	Context context;
	

	public UploadData(Context context) {
		super();
		this.context = context;
	}


	public void uploadData() throws InterruptedException, ExecutionException {
		SettingsHolder sh = new SettingsHolder(context);
		DataDbOperations dbOp = new DataDbOperations(context);
		String response = "Processing";
		if (dbOp.dataExists()) {
			WebServiceTask wst = new WebServiceTask(WebServiceTask.POST_TASK, context, response);
			wst.execute("http://" + sh.getServerIp() + ":8080/TalosServer/service/userservice/datas");
			response = wst.get();
			if (response.contains("Upload Suc")) {
				dbOp.clearData();
				Toast.makeText(context, "Upload Suceed", Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(context, "Upload Failed", Toast.LENGTH_LONG).show();
			}
		}
	}

}
