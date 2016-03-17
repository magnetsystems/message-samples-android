package com.magnet.magnetchat.callbacks;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
public abstract class EndlessLinearRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

  // The minimum amount of items to have below your current scroll position
  // before loading more.
  private int visibleThreshold = 10;
  // The current offset index of data you have loaded
  private int currentPage = 0;
  // The total number of items in the dataset after the last load
  private int previousTotalItemCount = 0;
  // True if we are still waiting for the last set of data to load.
  private boolean loading = true;
  // Sets the starting page index
  private int startingPageIndex = 0;

  private LinearLayoutManager mLinearLayoutManager;

  public EndlessLinearRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
    this.mLinearLayoutManager = layoutManager;
  }

  // This happens many times a second during a scroll, so be wary of the code you place here.
  // We are given a few useful parameters to help us work out if we need to load some more data,
  // but first we check if we are waiting for the previous load to finish.
  @Override
  public void onScrolled(RecyclerView view, int dx, int dy) {
    int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
    int visibleItemCount = view.getChildCount();
    int totalItemCount = mLinearLayoutManager.getItemCount();

    // If the total item count is zero and the previous isn't, assume the
    // list is invalidated and should be reset back to initial state
    if (totalItemCount < previousTotalItemCount) {
      this.currentPage = this.startingPageIndex;
      this.previousTotalItemCount = totalItemCount;
      if (totalItemCount == 0) {
        this.loading = true;
      }
    }
    // If it’s still loading, we check to see if the dataset count has
    // changed, if so we conclude it has finished loading and update the current page
    // number and total item count.
    if (loading && (totalItemCount > previousTotalItemCount)) {
      loading = false;
      previousTotalItemCount = totalItemCount;
    }

    // If it isn’t currently loading, we check to see if we have breached
    // the visibleThreshold and need to reload more data.
    // If we do need to reload some more data, we execute onLoadMore to fetch the data.
    if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
      currentPage++;
      onLoadMore(currentPage, totalItemCount);
      loading = true;
    }
  }

  // Defines the process for actually loading more data based on page
  public abstract void onLoadMore(int page, int totalItemsCount);

}
