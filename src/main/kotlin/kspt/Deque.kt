package kspt

class Deque<T> {

    var backingList: MutableList<T> = arrayListOf()

    fun isEmpty() = backingList.isEmpty()
    fun isNotEmpty() = backingList.isNotEmpty()

    fun pushFront(element: T){
        backingList.add(0,element)
    }

    fun getFront(): T? {
        if (backingList.isEmpty()){
            return null
        }
        val value = backingList.first()
        popFront()
        return value
    }

    fun popFront() {
        if (backingList.isNotEmpty()) backingList.removeAt(0)
    }

    fun peekFront(): T? {
        return if (backingList.isNotEmpty()) backingList.first() else null
    }

    fun pushBack(element: T) {
        backingList.add(element)
    }

    fun getBack(): T? {
        if (backingList.isEmpty()){
            return null
        }
        val value = backingList.last()
        popBack()
        return value
    }

    fun popBack() {
        if (backingList.isNotEmpty()) backingList.removeAt(backingList.size - 1)
    }

    fun peekBack(): T? {
        return if (backingList.isNotEmpty()) backingList.last() else null
    }

    fun clear() {
        backingList.clear()
    }
}