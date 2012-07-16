package spider.mineral

import spider.LinkFinderComponent
import xml.Document

/**
 * Created with IntelliJ IDEA.
 * User: ladlestein
 * Date: 7/9/12
 * Time: 6:30 PM
 * To change this template use File | Settings | File Templates.
 */

trait MineralFinderComponent extends LinkFinderComponent {

  val linkfinder = new LinkFinder {
    def links(page: Document) {

    }
  }
}
