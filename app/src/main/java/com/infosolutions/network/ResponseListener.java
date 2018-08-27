package com.infosolutions.network;


import com.android.volley.VolleyError;

public interface ResponseListener
{
	public void onSuccess(VolleySingleton.CallType type, String response);

	public void onFailure(VolleySingleton.CallType type, VolleyError error);
}
