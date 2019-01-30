package id.net.gmedia.pal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import id.net.gmedia.pal.Util.AppSharedPreferences;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        if(AppSharedPreferences.isLoggedIn(this)){
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}
