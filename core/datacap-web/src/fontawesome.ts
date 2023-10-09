import {FontAwesomeIcon} from '@fortawesome/vue-fontawesome'
import {library} from '@fortawesome/fontawesome-svg-core'
import {
  faAnglesLeft,
  faAnglesRight,
  faArrowLeft,
  faArrowRight,
  faArrowUpAZ,
  faCircle,
  faCircleInfo,
  faClock,
  faColumns,
  faDatabase,
  faDroplet,
  faFilter,
  faFont,
  faKey,
  faRepeat,
  faSun,
  faTable,
  faTextHeight,
  faTimes,
  faGear
} from '@fortawesome/free-solid-svg-icons'

/**
 * Creates icons for the given app.
 *
 * @param {any} app - The app object.
 */
const createIcons = (app: any) => {
  library.add(faArrowRight,
    faGear,
    faAnglesLeft,
    faAnglesRight,
    faFilter,
    faDatabase,
    faTable,
    faColumns,
    faKey,
    faRepeat,
    faDroplet,
    faCircle,
    faCircleInfo,
    faTimes,
    faClock,
    faFont,
    faArrowUpAZ,
    faSun,
    faTextHeight,
    faArrowLeft)
  app.component('FontAwesomeIcon', FontAwesomeIcon)
}

export {
  createIcons
}
