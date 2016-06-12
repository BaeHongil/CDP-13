package kr.ac.knu.odego.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
import kr.ac.knu.odego.R;
import kr.ac.knu.odego.common.RealmTransaction;
import kr.ac.knu.odego.common.RouteType;
import kr.ac.knu.odego.interfaces.BeaconBusDestListener;
import kr.ac.knu.odego.item.BusStop;
import kr.ac.knu.odego.item.Favorite;

/**
 * Created by Brick on 2016-06-09.
 */
public class BeaconListAdapter extends RealmBaseAdapter<BusStop> {
    private Realm mRealm;
    private int busOnImg, busOnFinalImg, busOnFirstImg;
    private int busOffImg, busOffFinalImg, busOffFirstImg;
    private int goalImg, goalFinalImg;
    private int budIdBackgroundColor;

    private BeaconBusDestListener goalListener;

    private ImageView destBusIcon;
    private TextView destBusId;

    private String busId;
    private int presentPosition;
    private String routeType;

    private final String MAIN = "main";
    private final String BRANCH = "branch";
    private final String CIRCULAR = "circular";
    private final String EXPRESS = "express";

    private int startIndex;
    private int curIndex;
    private int destIndex;

    public BeaconListAdapter(Context context, OrderedRealmCollection<BusStop> data, Realm mRealm, String routeType, String busId, int startIndex, int destIndex) {
        super(context, data);
        this.mRealm = mRealm;
        this.busId = busId;
        this.routeType = routeType;
        this.startIndex = startIndex;
        this.curIndex = startIndex;
        this.destIndex = destIndex;

        String type;
        if( routeType.equals( RouteType.MAIN.getName() ) )
            type = MAIN;
        else if( routeType.equals( RouteType.BRANCH.getName() ) )
            type = BRANCH;
        else if( routeType.equals( RouteType.CIRCULAR.getName() ) )
            type = CIRCULAR;
        else
            type = EXPRESS;

        Resources res = context.getResources();
        String packageName = context.getPackageName();
        busOnImg = res.getIdentifier("busposinfo_"+type+"_bus_on", "drawable", packageName );
        busOffImg = res.getIdentifier("busposinfo_"+type+"_bus_off", "drawable", packageName );
        budIdBackgroundColor = ContextCompat.getColor(context,
                res.getIdentifier(type+"_bus_dark", "color", packageName ));
        busOnFirstImg = res.getIdentifier("busposinfo_"+type+"_bus_on_first", "drawable", packageName );
        busOnFinalImg = res.getIdentifier("busposinfo_"+type+"_bus_on_final", "drawable", packageName );
        busOffFirstImg = res.getIdentifier("busposinfo_"+type+"_bus_off_first", "drawable", packageName );
        busOffFinalImg = res.getIdentifier("busposinfo_"+type+"_bus_off_final", "drawable", packageName );
        goalImg = res.getIdentifier("beacon_"+type+"_goal", "drawable", packageName );
        goalFinalImg = res.getIdentifier("beacon_"+type+"_goal_final", "drawable", packageName );
    }

    public void setCurIndex(int curIndex) {
        this.curIndex = curIndex;
        notifyDataSetChanged();
    }

    public void setBusDestListener(BeaconBusDestListener goalListener) {
        this.goalListener = goalListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = null;
        ViewHolder viewHolder = null;

        BusStop mBusStop = getItem(position);
        if(convertView==null) {
            itemView = inflater.inflate(R.layout.activity_busposinfo_list_item, parent, false);
            viewHolder = new ViewHolder(itemView);
            viewHolder.busStopId = mBusStop.getId();
            itemView.setTag( viewHolder );
        } else {
            itemView = convertView;
            viewHolder = (ViewHolder) itemView.getTag();
            viewHolder.busStopId = mBusStop.getId();
        }
        viewHolder.position = position;

        // 즐겨찾기 설정
        if( mRealm.where(Favorite.class).equalTo("mBusStop.id", mBusStop.getId()).count() > 0 )
            viewHolder.favoriteBtn.setChecked(true);
        else
            viewHolder.busStopName.setText(mBusStop.getName());
        viewHolder.busStopNo.setText(mBusStop.getNo());


        // 버스 있음
        if( position == curIndex  ) {
            if( position == 0 ) // 첫번째 정류장
                viewHolder.busIcon.setImageResource( busOnFirstImg );
            else if( position == getCount() - 1 ) // 마지막 정류장
                viewHolder.busIcon.setImageResource( busOnFinalImg );
            else
                viewHolder.busIcon.setImageResource(busOnImg);

            String busIdNum = busId.substring(busId.length() - 4, busId.length());
            viewHolder.busId.setText(busIdNum);
            viewHolder.busId.setTextColor(Color.WHITE);
            viewHolder.busId.setBackgroundColor(budIdBackgroundColor);
            viewHolder.busId.setVisibility(View.VISIBLE);

            return itemView;
        }

        // 버스 최초 출발지
        if( position == startIndex ) {
            if( position == getCount() - 1 )
                viewHolder.busIcon.setImageResource(goalFinalImg);
            else
                viewHolder.busIcon.setImageResource(goalImg);

            viewHolder.busId.setText("출발지");
            viewHolder.busId.setTextColor(budIdBackgroundColor);
            viewHolder.busId.setBackgroundColor(Color.WHITE);
            viewHolder.busId.setVisibility(View.VISIBLE);

            return itemView;
        }

        if( position == destIndex ) {
            if( position == getCount() - 1 )
                viewHolder.busIcon.setImageResource(goalFinalImg);
            else
                viewHolder.busIcon.setImageResource(goalImg);

            viewHolder.busId.setText("도착지");
            viewHolder.busId.setTextColor(budIdBackgroundColor);
            viewHolder.busId.setBackgroundColor(Color.WHITE);
            viewHolder.busId.setVisibility(View.VISIBLE);

            return itemView;
        }

        // 버스 없음
        if( position == 0 ) // 첫번째 정류장
            viewHolder.busIcon.setImageResource( busOffFirstImg );
        else if( position == getCount() - 1 ) // 마지막 정류장
            viewHolder.busIcon.setImageResource( busOffFinalImg );
        else
            viewHolder.busIcon.setImageResource(busOffImg);
        viewHolder.busId.setVisibility(View.INVISIBLE);

        return itemView;
    }

    private class ViewHolder implements View.OnClickListener {
        public String busStopId;

        public View itemView;
        public ToggleButton favoriteBtn;
        public TextView busStopName;
        public TextView busStopNo;
        public ImageView busIcon;
        public TextView busId;

        public int position;

        public ViewHolder(View itemView) {
            this.itemView = itemView;

            itemView.setOnClickListener(this);
            favoriteBtn = (ToggleButton) itemView.findViewById(R.id.favorite_btn);
            favoriteBtn.setOnClickListener(this);
            busStopName = (TextView) itemView.findViewById(R.id.busstop_name);
            busStopNo = (TextView) itemView.findViewById(R.id.busstop_no);
            busIcon = (ImageView) itemView.findViewById(R.id.bus_icon);
            busId = (TextView) itemView.findViewById(R.id.bus_id);

        }

        @Override
        public void onClick(View v) {
            // 즐겨찾기 버튼 선택
            if( v == favoriteBtn ) {
                if( !favoriteBtn.isChecked() ) { // 즐겨찾기 해제
                    RealmTransaction.deleteBusStopFavorite(mRealm, busStopId);
                    return;
                }
                // 즐겨찾기 설정
                RealmTransaction.createBusStopFavorite(mRealm, busStopId);
                return;
            }

            // 전체 뷰 선택
            if( v == itemView ) {
                if (position > curIndex) { // 현재 위치보다 멀리 있는 도착지 선택시
                    // 이미 선택한 목적지를 다시 선택할 때
                    if (position == destIndex) {
                        destIndex = -1;
                        goalListener.onChangeBusDest("목적지를 선택하세요.", destIndex);
                        return;
                    } else {
                        destIndex = position;
                        goalListener.onChangeBusDest(busStopName.getText().toString(), destIndex);
                    }
                    notifyDataSetChanged();
                } else // 현재위치보다 앞에 도착지 클릭 시
                    Toast.makeText(context, context.getString(R.string.reject_set_destnation), Toast.LENGTH_SHORT).show();

                return;
            }
        }
    }

}
