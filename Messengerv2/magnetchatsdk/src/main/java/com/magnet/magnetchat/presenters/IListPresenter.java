/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
package com.magnet.magnetchat.presenters;

import com.magnet.magnetchat.ui.adapters.BaseSortedAdapter;

public interface IListPresenter<T> {

  void onLoad(int offset, int limit);

  void onSearch(String query, String sort);

  void onSearchReset();

  void onItemSelect(int position, T item);

  void onItemLongClick(int position, T item);

  BaseSortedAdapter.ItemComparator<T> getItemComparator();
}
