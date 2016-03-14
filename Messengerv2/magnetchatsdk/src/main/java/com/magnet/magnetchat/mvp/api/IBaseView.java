package com.magnet.magnetchat.mvp.api;

/**
 * Copyright (c) 2012-2016 Magnet Systems. All rights reserved.
 */
public interface IBaseView {

  /**
   * Set the title in the actionbar
   * @param title
   */
  void setTitle(String title);

  /**
   * Show or hide the progress bar
   *
   * @param active
   */
  void setProgressIndicator(boolean active);
}
