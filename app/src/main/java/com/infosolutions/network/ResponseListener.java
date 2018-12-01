package com.infosolutions.network;


import com.android.volley.VolleyError;

public interface ResponseListener
{
  void onSuccess(VolleySingleton.CallType type, String response);

  void onFailure(VolleySingleton.CallType type, VolleyError error);
}
