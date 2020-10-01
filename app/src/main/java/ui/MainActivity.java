package ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.barberme.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    final String TAG = "MainActivity";
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    FirebaseAuth.AuthStateListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.toolbar);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
        View headerView = navigationView.getHeaderView(0);
        String welcome = "Welcome";
        TextView welcomeTV = headerView.findViewById(R.id.navigation_header_tv);
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        welcomeTV.setText(welcome +" "+ currentUser.getDisplayName());
        welcomeTV.setMovementMethod(LinkMovementMethod.getInstance());
        getSupportFragmentManager().beginTransaction().add(R.id.container, new AllBarberShopsFragment(), TAG).commit();

        firebaseListener = firebaseAuth -> {
            if(firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(MainActivity.this, SignInUpActivity.class);
                startActivity(intent);
                finish();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        unCheckAllMenuItems(navigationView.getMenu());
        item.setChecked(true);
        drawerLayout.closeDrawers();
        switch (item.getItemId())
        {
            case R.id.all_barbershops:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new AllBarberShopsFragment(), TAG).addToBackStack(null).commit();
                break;
            case R.id.my_barbershops:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, new MyBarberShopsFragment(), TAG).addToBackStack(null).commit();
                break;
            case R.id.Logout:
                logout();
                break;
        }
        return false;
    }

    private void unCheckAllMenuItems(@NonNull final Menu menu) {
        int size = menu.size();
        for (int i = 0; i < size; i++) {
            final MenuItem item = menu.getItem(i);
            if(item.hasSubMenu()) {
                // Un check sub menu items
                unCheckAllMenuItems(item.getSubMenu());
            } else {
                item.setChecked(false);
            }
        }
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Do you want to logout?")
                .setIcon(R.mipmap.ic_launcher)
                .setPositiveButton(("Yes"), (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                })
                .setNegativeButton(("No"), null)
                .create()
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(firebaseListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseListener != null)
            firebaseAuth.removeAuthStateListener(firebaseListener);
    }
}