package kr.ac.knu.odego.item;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import kr.ac.knu.odego.common.RouteType;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by BHI on 2016-05-14.
 */
@Getter
@Setter
public class Route extends RealmObject {
    @PrimaryKey
    private String id; // 노선고유ID
    @Index
    private String no; // 버스번호
    @Index
    private String direction; // 방면
    private String type; // 노선유형
    private String startBusStopName;
    private String endBusStopName;
    private int startHour;
    private int startMin;
    private int endHour;
    private int endMin;
    private int interval;
    private int intervalSat;
    private int intervalSun;
    private Date updatedDetail;
    @Index
    private int historyIndex; // 최근기록

    public void setEnumBusType(RouteType routeType) {
        setType( routeType.name() );
    }

    public RouteType getEnumBusType() {
        return RouteType.valueOf(type);
    }
}
