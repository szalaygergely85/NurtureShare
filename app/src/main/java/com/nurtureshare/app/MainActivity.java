package com.nurtureshare.app;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.nurtureshare.app.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    // Current destination id
    private int currentDestinationId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bottomNav.setItemActiveIndicatorColor(
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.primary_container)));

        setupNavigation();
        setupFab();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);

        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavigationUI.setupWithNavController(binding.bottomNav, navController);

            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                currentDestinationId = destination.getId();
                updateFabVisibility(destination.getId());
            });
        }
    }

    private void setupFab() {
        // Tasks screen uses its own FAB inside fragment_tasks.xml.
        // This global FAB is only used for the Notes screen.
        binding.fab.setOnClickListener(v -> {
            if (currentDestinationId == R.id.notesFragment) {
                navController.navigate(R.id.action_notes_to_addNote);
            }
        });
    }

    private void updateFabVisibility(int destinationId) {
        binding.fab.setVisibility(destinationId == R.id.notesFragment ? View.VISIBLE : View.GONE);
    }

}
