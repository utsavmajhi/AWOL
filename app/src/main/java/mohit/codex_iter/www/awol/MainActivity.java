package mohit.codex_iter.www.awol;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieProperty;
import com.airbnb.lottie.model.KeyPath;
import com.airbnb.lottie.value.LottieFrameInfo;
import com.airbnb.lottie.value.SimpleLottieValueCallback;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseThemedActivity {
    EditText user, pass;
    Button btn;
    SharedPreferences userm, logout;
    SharedPreferences.Editor edit;
    private LottieAnimationView animationView;
    private LinearLayout l2;
    private final int frames = 9;
    private int currentAnimationFrame = 0;
    private boolean track;
    private boolean login;
    private TextView logo;
    private String param_0, param_1, response_d;



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Constants.setDarkStatusBar(this);
        Bundle extras = getIntent().getExtras();
        String status = "";
        if (extras != null) {
            status = extras.getString("logout_status");
        }
        SharedPreferences status_lg = this.getSharedPreferences("status", 0);
        SharedPreferences.Editor editor = status_lg.edit();

        editor.putString("status", status);
        editor.apply();

        animationView = findViewById(R.id.progress_lottie);
        resetAnimationView();
        animationView.addValueCallback(
                new KeyPath("**"),
                LottieProperty.COLOR_FILTER,
                new SimpleLottieValueCallback<ColorFilter>() {
                    @Override
                    public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                        return new PorterDuffColorFilter(getResources().getColor(R.color.darkColorAccent), PorterDuff.Mode.SRC_ATOP);
                    }
                }
        );
        l2 = findViewById(R.id.linearLayout2);
        logo = findViewById(R.id.logo);
        logo.setVisibility(View.VISIBLE);
        l2.setVisibility(View.VISIBLE);
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        btn = findViewById(R.id.btn);
        userm = getSharedPreferences("user",
                Context.MODE_PRIVATE);
        logout = getSharedPreferences("sub",
                Context.MODE_PRIVATE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String u = user.getText().toString().trim();
                String p = pass.getText().toString().trim();

                if (u.equals("") || p.equals(""))
                    Toast.makeText(MainActivity.this, "Enter your Details", Toast.LENGTH_SHORT).show();

                else {
                    if (haveNetworkConnection()) {
                        String web = getString(R.string.link);
                        getData(web, u, p);
                        edit = userm.edit();
                        edit.putString("user", u);
                        edit.putString(u + "pass", p);
                        edit.putString("pass", p);
                        edit.apply();
                        edit = logout.edit();
                        edit.putBoolean("logout", false);
                        edit.apply();
                    } else {
                        Toast.makeText(getApplicationContext(), "Something, went wrong.Try Again", Toast.LENGTH_SHORT).show();
                        user.setText("");
                        pass.setText("");
                    }
//                    else {
                    // showData(u, p);
//                    }
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);
                }
            }
        });

        if (userm.contains("user") && userm.contains("pass") && logout.contains("logout") && !logout.getBoolean("logout", false)) {
            user.setText(userm.getString("user", ""));
            pass.setText(userm.getString("pass", ""));
            btn.performClick();
        }


    }


//    private void showData(String u, String p) {
//        Toast.makeText(getApplicationContext(), "Something, went wrong.Try Again", Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(MainActivity.this, home.class);
//        String test = userm.getString("user", "");
//        if(userm.contains(u)) {
//            if(p.equals(userm.getString(u+"pass", ""))){
//                edit=logout.edit();
//                edit.putBoolean("logout",false);
//                edit.apply();
//                String s = userm.getString(u, "");
//                intent.putExtra("result", s);
//                Toast.makeText(getApplicationContext(), "showing offline value for this user", Toast.LENGTH_SHORT).show();
//                startActivity(intent);
//            }else {
//                Toast.makeText(getApplicationContext(), "invalid credentials", Toast.LENGTH_SHORT).show();
//            }
//        }
//        else
//        Toast.makeText(getApplicationContext(), "No offline data for the user", Toast.LENGTH_SHORT).show();

    //}

    private void resetAnimationView() {
        currentAnimationFrame = 0;
        animationView.addValueCallback(new KeyPath("**"), LottieProperty.COLOR_FILTER,
                new SimpleLottieValueCallback<ColorFilter>() {
                    @Override
                    public ColorFilter getValue(LottieFrameInfo<ColorFilter> frameInfo) {
                        return null;
                    }
                }
        );
    }

    private void getData(final String... param) {

//        pd = new ProgressDialog(MainActivity.this);
//        pd.setMessage("Please wait");
//        pd.setCancelable(false);
        animationView.setVisibility(View.VISIBLE);
        l2.setVisibility(View.INVISIBLE);
        logo.setVisibility(View.INVISIBLE);
        //pd.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        param_0 = param[0];
        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0] + "/attendance",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        response_d = response;
                        if (response.equals("404")) {
                            if (animationView.getVisibility() == View.VISIBLE)
                                animationView.setVisibility(View.INVISIBLE);
                            l2.setVisibility(View.VISIBLE);
                            logo.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                        } else {
                            login = true;
                            Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, home.class);
                            //getname(param);
                            param_1 = param[1];
                            response += "kkk" + param[1];
                            intent.putExtra("result", response);
                            edit.putString(param[1], response);
                            edit.apply();
                            startActivity(intent);

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        if (animationView.getVisibility() == View.VISIBLE)
                            animationView.setVisibility(View.INVISIBLE);

                        //showData(param[1], param[2]);
                        if (error instanceof AuthFailureError) {
                            l2.setVisibility(View.VISIBLE);
                            logo.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Wrong Credentials!", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof ServerError) {
                            l2.setVisibility(View.VISIBLE);
                            logo.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Cannot connect to ITER servers right now.Try again", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof NetworkError) {
                            Log.e("Volley_error", String.valueOf(error));
                            l2.setVisibility(View.VISIBLE);
                            logo.setVisibility(View.VISIBLE);
                            Toast.makeText(getApplicationContext(), "Cannot establish connection", Toast.LENGTH_SHORT).show();
                        } else if (error instanceof TimeoutError) {
                            if (!track) {
                                animationView.setVisibility(View.VISIBLE);
                                track = true;
                                btn.performClick();
                            } else {
                                l2.setVisibility(View.VISIBLE);
                                logo.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), "Cannot connect to ITER servers right now.Try again", Toast.LENGTH_SHORT).show();
                                track = false;
                            }
                        }

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", param[1]);
                params.put("pass", param[2]);

                return params;
            }
        };
        queue.add(postRequest);


    }
//    private void getname(final String... param){
//        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
//        StringRequest postRequest = new StringRequest(Request.Method.POST, param[0]+"/studentinfo",
//                new Response.Listener<String>()
//                {
//                    @Override
//                    public void onResponse(String response)  {
//
//                     try {
//                           JSONObject jobj  = new JSONObject(response);
//                           JSONArray jarr   = jobj.getJSONArray("detail");
//                           JSONObject jobj1 = jarr.getJSONObject(0);
//                           name = jobj1.getString("name");
//                        } catch (JSONException e) {
//                            Toast.makeText(getApplicationContext(), "cannot fetch name!!", Toast.LENGTH_SHORT).show();
//                        }
//
//                    }
//                },
//                new Response.ErrorListener()
//                {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {}}
//
//        ) {
//            @Override
//            protected Map<String, String> getParams()
//            {
//                Map<String, String>  params = new HashMap<String, String>();
//                params.put("user", param[1]);
//                params.put("pass", param[2]);
//
//                return params;
//            }
//        };
//        queue.add(postRequest);
//
//    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm != null ? cm.getActiveNetworkInfo() : null;
        // connected to the internet


        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                haveConnectedWifi = true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to mobile data
                haveConnectedMobile = true;
            }
        }

        return haveConnectedWifi || haveConnectedMobile;
    }
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void onResume(){
        super.onResume();
        if (login){
            Intent intent = new Intent(MainActivity.this, home.class);
            //getname(param);
            response_d += "kkk" + param_1;
            intent.putExtra("result", response_d);
            edit.putString(param_1, response_d);
            edit.apply();
            startActivity(intent);
        }
    }
}
