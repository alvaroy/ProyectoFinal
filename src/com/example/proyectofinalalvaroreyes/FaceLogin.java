package com.example.proyectofinalalvaroreyes;

import java.util.Arrays;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.UserSettingsFragment;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FaceLogin extends FragmentActivity {
	
	UserSettingsFragment fragmentsetting;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (savedInstanceState==null) {
			setContentView(R.layout.layout_facelogin);
			fragmentsetting = new UserSettingsFragment();
			fragmentsetting.setSessionStatusCallback(new StatusCallback() {
				
				@Override
				public void call(Session session, SessionState state, Exception exception) {
					// TODO Auto-generated method stub
					OnSessionStateChanged(session, state, exception);
				}
			});
			fragmentsetting.setReadPermissions(Arrays.asList("user_likes", "user_status", "email"));
			getSupportFragmentManager()
			.beginTransaction().
			add(R.id.facecontainer, fragmentsetting)
			.commit();
		}
		
	}
	
	public void OnSessionStateChanged(Session session, SessionState state, Exception exception) {
		// TODO Auto-generated method stub
		
		if (state == SessionState.OPENED){
			Request req = Request.newMeRequest(session, new Request.GraphUserCallback() {
				
				@Override
				public void onCompleted(GraphUser user, Response response) {
					Bundle args = new Bundle();
					args.putString("email", (String) user.getProperty("email"));
					Map map = new Map();
					getSupportFragmentManager().beginTransaction().replace(R.id.facecontainer, map).addToBackStack("FB").commit();
				}
			});
			req.executeAsync();			
		}
		
		if (state == SessionState.CLOSED_LOGIN_FAILED) {
		}
	}
		
}
