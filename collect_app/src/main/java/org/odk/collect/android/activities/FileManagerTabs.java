/*
 * Copyright (C) 2017 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.odk.collect.android.activities;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import org.fieldsight.collect.android.R;
import org.fieldsight.naxa.fragments.FSDataManagerList;
import org.fieldsight.naxa.login.model.Site;
import org.odk.collect.android.adapters.ViewPagerAdapter;
import org.odk.collect.android.views.SlidingTabLayout;

import java.util.ArrayList;

import static org.fieldsight.naxa.common.Constant.EXTRA_OBJECT;

public class FileManagerTabs extends CollectAbstractActivity {

//    private final DataManagerList dataManagerList = DataManagerList.newInstance();

    private Site loadedSite;

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(getString(R.string.manage_files));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.file_manager_tabs);
        initToolbar();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loadedSite = bundle.getParcelable(EXTRA_OBJECT);
        }


//        String[] tabNames = {getString(R.string.data), getString(R.string.FORMS)};
        String[] tabNames = {getString(R.string.data)};
        // Get the ViewPager and set its PagerAdapter so that it can display items
        ViewPager viewPager = findViewById(R.id.pager);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(FSDataManagerList.newInstance(loadedSite));
//        fragments.add(formManagerList);

        viewPager.setAdapter(new ViewPagerAdapter(
                getSupportFragmentManager(), tabNames, fragments));

        // Give the SlidingTabLayout the ViewPager
        SlidingTabLayout slidingTabLayout = findViewById(R.id.tabs);
        // Attach the view pager to the tab strip
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setFontColor(android.R.color.white);
        slidingTabLayout.setBackgroundColor(Color.DKGRAY);
        slidingTabLayout.setViewPager(viewPager);

        slidingTabLayout.setVisibility(View.GONE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.notes);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
