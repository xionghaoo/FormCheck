package com.zdtco.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.xh.formlib.R;
import com.zdtco.datafetch.data.AuditStatus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/25.
 */

public class AuditView extends LinearLayout {
//    private AuditItemView activityView;
    private int activityViewIndex;
//    private AuditItemView pendingView;
    private int pendingViewIndex;
    private AuditItemView lastAuditView;

    boolean isLastAudit;

    private List<AuditItemView> itemViewList = new ArrayList<>();

    public AuditView(Context context) {
        super(context);
//        init(context);
    }

    public AuditView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
//        init(context);
    }

    public void init(Context context, List<AuditStatus> statusList) {
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View v = layoutInflater.inflate(R.layout.widget_audit_view_header, this, false);
        addView(v);
        View splitLine = layoutInflater.inflate(R.layout.widget_audit_view_split_line, this, false);
        addView(splitLine);
        boolean isShowTodoAudit = false;
        boolean isPendingAudit = false;
        for (int i = 1; i <= 6; i++) {
            AuditItemView itemView = new AuditItemView(context);
            itemViewList.add(itemView);
            itemView.init(context, i);
            itemView.setOnIndexClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    lastAuditView.showIndexCircle(false);
                    itemViewList.get(activityViewIndex).showIndexCircle(true);
                    isLastAudit = false;
                }
            });
//            if (i == 7) {
//                itemView.hideRefuseButton();
//            }
            addView(itemView);
//            itemView.index = i;
            View sl = layoutInflater.inflate(R.layout.widget_audit_view_split_line, this, false);
            addView(sl);

            int statusIndex = i - 1;
            if (statusIndex < statusList.size()) {
                AuditStatus status = statusList.get(statusIndex);
                if (status.hasAudit) {
                    itemView.hasAudit(status.userWorkNo, status.judgement);
                } else {
                    if (!isShowTodoAudit) {
                        itemView.todoAudit();
                        itemView.showIndexCircle(true);
                        activityViewIndex = statusIndex;
                        isShowTodoAudit = true;
                    } else {
                        if (!isPendingAudit) {
//                            pendingView = itemView;
                            pendingViewIndex = statusIndex;
                            isPendingAudit = true;
                        }
                        itemView.noAudit();
                    }
                }
            }
        }
        AuditItemView itemView = new AuditItemView(context);
        itemViewList.add(itemView);
        itemView.init(context, 7);
        itemView.lastAudit();
        lastAuditView = itemView;
        lastAuditView.setOnIndexClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lastAuditView.showIndexCircle(true);
                itemViewList.get(activityViewIndex).showIndexCircle(false);
                isLastAudit = true;
            }
        });
        addView(itemView);
        View sl = layoutInflater.inflate(R.layout.widget_audit_view_split_line, this, false);
        addView(sl);
    }

    public void onAuditClickListener(OnClickListener listener) {
        itemViewList.get(activityViewIndex).onAuditClick(listener);
    }

    public void onRefuseClickListener(OnClickListener listener) {
        itemViewList.get(activityViewIndex).onRefuseClick(listener);
    }

    public void onLastAuditClick(OnClickListener listener) {
        lastAuditView.onAuditClick(listener);
    }

    public void audit() {
        itemViewList.get(activityViewIndex).audit();
        itemViewList.get(activityViewIndex).showIndexCircle(false);

        //取消最终核准状态
        lastAuditView.showIndexCircle(false);
        isLastAudit = false;
    }

    public void disableCurrentAudit() {
        itemViewList.get(activityViewIndex).audit();
    }

    public void lastAudit() {
        lastAuditView.audit();
    }

    public void setActivityViewWorkNo(String workNo) {
        if (isLastAudit) {
            lastAuditView.setWorkNo(workNo);
        } else {
            itemViewList.get(activityViewIndex).setWorkNo(workNo);
        }
    }

    public void nextAuditShow(final Callback callback) {
        if (activityViewIndex >= 5) {
            isLastAudit = true;
            return;
        }
        itemViewList.get(pendingViewIndex).todoAudit();
        itemViewList.get(pendingViewIndex).showIndexCircle(true);
        activityViewIndex = pendingViewIndex;
        pendingViewIndex ++;
        itemViewList.get(activityViewIndex).onAuditClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onAudited();
            }
        });

        itemViewList.get(activityViewIndex).onRefuseClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onRefuse();
            }
        });
    }

    public int getActivityIndex() {
        return activityViewIndex;
    }

    public String getJudgement() {
        if (isLastAudit) {
            return lastAuditView.getJudgement();
        } else {
            return itemViewList.get(activityViewIndex).getJudgement();
        }
    }
    public String getWorkNo() {
        if (isLastAudit) {
            return lastAuditView.getWorkNo();
        } else {
            return itemViewList.get(activityViewIndex).getWorkNo();
        }
    }

    public interface Callback {
        void onAudited();
        void onRefuse();
    }
}
