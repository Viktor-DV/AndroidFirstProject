import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.dolgantsev.androindfirstproject.Film
import com.dolgantsev.androindfirstproject.R

// В параметр передаем слушатель, чтобы обрабатывать нажатия
class FilmListRecyclerAdapter(private val clickListener: OnItemClickListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Список элементов для RecyclerView
    private val items = mutableListOf<Film>()

    // Возвращаем количество элементов в списке
    override fun getItemCount() = items.size

    // Привязываем ViewHolder и инфлейтим разметку
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.film_item, parent, false)
        return FilmViewHolder(view)
    }

    // Привязываем данные из объекта Film в View из film_item.xml
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //Проверяем, какой у нас ViewHolder
        when (holder) {
            is FilmViewHolder -> {
                //Вызываем метод bind(), который мы создали, и передаем туда объект
                //из нашей базы данных с указанием позиции
                holder.bind(items[position])
                //Обрабатываем нажатие на весь элемент целиком(можно сделать на отдельный элемент
                //например, картинку) и вызываем метод нашего листенера, который мы получаем из
                //конструктора адаптера
                holder.itemView.findViewById<CardView>(R.id.item_container).setOnClickListener {
                    clickListener.click(items[position])
                }
            }
        }
    }

    // Метод для добавления элементов в список
    fun addItems(list: List<Film>) {
        items.clear()  // Очищаем текущий список
        items.addAll(list)  // Добавляем новый список
        notifyDataSetChanged()  // Обновляем RecyclerView
    }

    // Интерфейс для обработки кликов
    interface OnItemClickListener {
        fun click(film: Film)  // Обрабатываем клик по фильму
    }
}