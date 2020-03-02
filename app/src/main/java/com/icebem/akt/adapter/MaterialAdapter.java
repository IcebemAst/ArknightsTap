package com.icebem.akt.adapter;

import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.icebem.akt.R;
import com.icebem.akt.app.PreferenceManager;
import com.icebem.akt.app.ResolutionConfig;
import com.icebem.akt.model.MaterialInfo;
import com.icebem.akt.overlay.OverlayToast;

import org.json.JSONException;

import java.io.IOException;

public class MaterialAdapter extends RecyclerView.Adapter<MaterialAdapter.ViewHolder> {
    private static final String RES_START_MTL = "mtl_";
    private static final String RES_START_BG = "bg_mtl_t";
    private static final String RES_TYPE = "mipmap";
    private static final int[] mtlId = {
            30014, 30024, 30034, 30044, 30054, 30064,
            30013, 30023, 30033, 30043, 30053, 30063,
            30012, 30022, 30032, 30042, 30052, 30062,
            30011, 30021, 30031, 30041, 30051, 30061,
            30073, 30083, 30093, 30103, 31013, 31023,
            30074, 30084, 30094, 30104, 31014, 31024
    };
    private int spanCount;
    private PreferenceManager manager;
    private MaterialInfo[] infoList;

    public MaterialAdapter(PreferenceManager manager, int spanCount) throws IOException, JSONException {
        this.manager = manager;
        this.spanCount = spanCount;
        infoList = MaterialInfo.load(manager.getContext());
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView view = new ImageView(parent.getContext());
        int size = view.getContext().getResources().getDimensionPixelOffset(R.dimen.control_padding) << 1;
        size = (ResolutionConfig.getAbsoluteHeight(view.getContext()) - size) / spanCount;
        view.setLayoutParams(new ViewGroup.LayoutParams(size, size));
        size >>= 4;
        view.setPadding(size, size, size, size);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MaterialInfo info = findMaterialById(mtlId[position]);
        if (info == null) return;
        ImageView view = (ImageView) holder.itemView;
        view.setBackgroundResource(view.getResources().getIdentifier(RES_START_BG + info.getStar(), RES_TYPE, view.getContext().getPackageName()));
        view.setImageResource(view.getResources().getIdentifier(RES_START_MTL + info.getId(), RES_TYPE, view.getContext().getPackageName()));
        view.setOnClickListener(v -> {
            StringBuilder builder = new StringBuilder();
            builder.append(info.getName(manager.getTranslationIndex()));
            if (info.getMissions() == null) {
                builder.append(System.lineSeparator());
                builder.append(v.getContext().getString(R.string.tip_material_workshop));
            } else {
                for (MaterialInfo.Mission mission : info.getMissions()) {
                    builder.append(System.lineSeparator());
                    int sanity = mission.getSanity();
                    float frequency = mission.getFrequency();
                    builder.append(v.getContext().getString(R.string.tip_material_mission, mission.getName(), frequency * 100, sanity / frequency));
                }
            }
            OverlayToast.show(v.getContext(), builder.toString(), OverlayToast.LENGTH_LONG);
        });
    }

    @Override
    public int getItemCount() {
        return mtlId.length;
    }

    private MaterialInfo findMaterialById(int id) {
        for (MaterialInfo info : infoList) {
            if (info.getId() == id)
                return info;
        }
        return null;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull ImageView itemView) {
            super(itemView);
        }
    }
}