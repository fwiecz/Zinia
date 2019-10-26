package de.hpled.zinia.xcolor

import java.io.Serializable
import java.util.ArrayList

class XcolorList : ArrayList<Xcolor>, Serializable {
    constructor() : super()
    constructor(size: Int) : super(size)
    constructor(size: Int, init: (Int) -> Xcolor) : super(List(size, init))
    constructor(vararg xcolors : Xcolor) : this(xcolors.size, {xcolors.get(it)})
}