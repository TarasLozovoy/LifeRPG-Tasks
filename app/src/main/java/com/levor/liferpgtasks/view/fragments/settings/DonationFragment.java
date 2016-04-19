package com.levor.liferpgtasks.view.fragments.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.view.fragments.DefaultFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DonationFragment extends DefaultFragment {
    @Bind(R.id.purchase_premium_text_view)  TextView purchasePremiumTextView;
    @Bind(R.id.purchase_premium_layout)     View purchasePremiumLayout;
    @Bind(R.id.donate_1_usd_layout)         View donate1USDLayout;
    @Bind(R.id.donate_2_usd_layout)         View donate2USDLayout;
    @Bind(R.id.donate_3_usd_layout)         View donate3USDLayout;
    @Bind(R.id.donate_5_usd_layout)         View donate5USDLayout;
    @Bind(R.id.donate_1_usd_text_view)      TextView donate1USDTextView;
    @Bind(R.id.donate_2_usd_text_view)      TextView donate2USDTextView;
    @Bind(R.id.donate_3_usd_text_view)      TextView donate3USDTextView;
    @Bind(R.id.donate_5_usd_text_view)      TextView donate5USDTextView;

    private String developerPayload;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_donation, container, false);
        ButterKnife.bind(this, v);

        if (getCurrentActivity().isPremium()) {
            purchasePremiumLayout.setVisibility(View.GONE);
        } else {
            String premiumText = getString(R.string.purchase_premium_text) + " " + getCurrentActivity().getPremiumPrice();
            purchasePremiumTextView.setText(premiumText);
        }

        String donationText = getString(R.string.donate_N_usd) + " " + getCurrentActivity().getPurchasePrice(getString(R.string.purchase_1_donation), "$1");
        donate1USDTextView.setText(donationText);

        donationText = getString(R.string.donate_N_usd) + " " + getCurrentActivity().getPurchasePrice(getString(R.string.purchase_2_donation), "$2");
        donate2USDTextView.setText(donationText);

        donationText = getString(R.string.donate_N_usd) + " " + getCurrentActivity().getPurchasePrice(getString(R.string.purchase_3_donation), "$3");
        donate3USDTextView.setText(donationText);

        donationText = getString(R.string.donate_N_usd) + " " + getCurrentActivity().getPurchasePrice(getString(R.string.purchase_5_donation), "$5");
        donate5USDTextView.setText(donationText);

        registerListeners();

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getCurrentActivity().isPremium() ?
                getString(R.string.donate)
                : getString(R.string.remove_ads));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getController().sendScreenNameToAnalytics("Donation Fragment");
    }

    private void registerListeners() {
        purchasePremiumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseItem(getString(R.string.purchase_premium));
            }
        });

        donate1USDLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseItem(getString(R.string.purchase_1_donation));
            }
        });

        donate2USDLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseItem(getString(R.string.purchase_2_donation));
            }
        });

        donate3USDLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseItem(getString(R.string.purchase_3_donation));
            }
        });

        donate5USDLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseItem(getString(R.string.purchase_5_donation));
            }
        });
    }

    private void purchaseItem(String itemKey) {
        developerPayload = itemKey + UUID.randomUUID().toString();
        boolean isPurchaseSuccessful = getCurrentActivity().performBuy(itemKey, developerPayload);
        if (!isPurchaseSuccessful) {
            Toast.makeText(getCurrentActivity(), getString(R.string.purchase_unsuccessful), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1001) {
            int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
            String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
            String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

            if (resultCode == Activity.RESULT_OK) {
                try {
                    JSONObject jo = new JSONObject(purchaseData);
                    String sku = jo.getString("productId");
                    String payload = jo.getString("developerPayload");
                    String purchaseToken = jo.getString("purchaseToken");
                    if (developerPayload.equals(payload)) {
                        if (sku.equals(getString(R.string.purchase_premium))) {
                            getCurrentActivity().switchPremiumMode(true);
                            purchasePremiumLayout.setVisibility(View.GONE);
                        } else {
                            getCurrentActivity().consumePurchase(purchaseToken);
                        }
                        getController().getGATracker().send(new HitBuilders.EventBuilder()
                                .setCategory(getCurrentActivity().getString(R.string.GA_action))
                                .setAction("Performed purchase of " + sku)
                                .setValue(1)
                                .build());
                        Toast.makeText(getCurrentActivity(), getString(R.string.purchase_successful), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getCurrentActivity(), getString(R.string.purchase_unsuccessful), Toast.LENGTH_LONG).show();
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getCurrentActivity(), getString(R.string.purchase_unsuccessful), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
