bible_by_den4ik
===============

Библия на Android
Бесплатная версия Библии, я хочу реализоватб все необходимые ии чуть боьше функции, например фунция сравнения разных ппереводов.
В приложении оочень простой интерфейс направлен для использования любого уровня пользователя.
В этой Библии используется БД SQLite, которую в ручную 2мя переводами заполняли 2 человека.

Нуждаюсь в поддержке, советами улучшениями.

Через некоторое время выйдет платная версия, в которой будут открываться функции синфронизации с DropBox, 
чтоб на любом вашем Android смартфоне у вас были все ваши заметки, планы, комментарии и т.д.

<b>Можно использовать ContentProvider  конце страницы описан</b>

<img src="http://i7.pixs.ru:/storage/0/2/8/Screenshot_2295865_10633028.png" alt="Screenshot 1"/>
<img src="http://i7.pixs.ru:/storage/0/3/1/Screenshot_3652500_10633031.png" alt="Screenshot 2"/>
<img src="http://i7.pixs.ru:/storage/0/3/3/Screenshot_6602533_10633033.png" alt="Screenshot 3"/>
<img src="http://i7.pixs.ru:/storage/0/3/5/Screenshot_9164020_10633035.png" alt="Screenshot 4"/>
<img src="http://i7.pixs.ru:/storage/0/3/7/Screenshot_6510172_10633037.png" alt="Screenshot 5"/>
<img src="http://i7.pixs.ru:/storage/0/3/8/Screenshot_4035385_10633038.png" alt="Screenshot 6"/>
<img src="http://i7.pixs.ru:/storage/0/4/1/Screenshot_6683648_10633041.png" alt="Screenshot 7"/>
<img src="http://i7.pixs.ru:/storage/0/4/4/Screenshot_8672038_10633044.png" alt="Screenshot 8"/>
<img src="http://i7.pixs.ru:/storage/0/4/5/Screenshot_6343591_10633045.png" alt="Screenshot 9"/>
<img src="http://i7.pixs.ru:/storage/0/4/6/Screenshot_3033145_10633046.png" alt="Screenshot 10"/>


ContentProvider для желающих создать виджеты или доп приложения.
Путь для получения глав, стихов, списка книг и т.д.
  ua.maker.gbible.provider.BibleContent

Пример:
	Получить Cursor с ид книг 
		<b>content://ua.maker.gbible.provider.BibleContent/ </b>
		
		Ид: public static final String FIELD_BOOK_ID = "bookId"; 
		Данные: Integer 1..66; получаете все строки вам необходимо добавлять их по индивидуальности, проверяя при получении.
		Код:
		<b><font color="#0000CD">
		List<String> books = new ArrayList<String>();
		int j = 0;
		if(db.isOpen()){
			Cursor cursor = db.rawQuery("SELECT "+FIELD_BOOK_ID+" FROM '"+tableName+"'", null);
			if(cursor.moveToFirst()){
				int indexBookId = cursor.getColumnIndex(FIELD_BOOK_ID);
				do{
					if(j != cursor.getInt(indexBookId)){
						books.add(Tools.getBookNameByBookId(cursor.getInt(indexBookId), mContext));
						j = cursor.getInt(indexBookId);
					}
				}while(cursor.moveToNext());
			}
		}
		</font></b>
	
	Получить Cursor с главами книги по ее ID (Список идов найдете по адресу в проекте ua.maker.gbible.constant.BooksId)
		<b>content://ua.maker.gbible.provider.BibleContent/<ID></b>
		<b>content://ua.maker.gbible.provider.BibleContent/10</b>  вы получите главы в книге по ид 10, тут так же необходимо отсортировать, проверкой
		такого типа:
		<b><font color="#0000CD">
			public List<Integer> getChapters(String tableName, int bookId){
				List<Integer> chapters = new ArrayList<Integer>();
				int count = 1;
				if(db.isOpen()){
					Cursor c = db.rawQuery("SELECT "+FIELD_BOOK_ID+", "+FIELD_POEM+" FROM '"+tableName+"'", null);
					if(c.moveToFirst()){
						int indexBookId = c.getColumnIndex(FIELD_BOOK_ID);
						int indexPoemId = c.getColumnIndex(FIELD_POEM);
						do{
							if(c.getInt(indexBookId) == bookId)
								if(c.getInt(indexPoemId) == 1){
									chapters.add(count);
									count++;
								}
							
						}while(c.moveToNext());
					}
				}		
				return chapters;
			}
			</font></b>
			
	Получить Cursor со стихами по конкретной главе и книге
		content://ua.maker.gbible.provider.BibleContent/<ID>/<Number Chapter>
		content://ua.maker.gbible.provider.BibleContent/10/14  Выдас нам стихи с главы 14 книга 2 Царств
		<b><font color="#0000CD">Код по получению инфы с курсора
			if(cursor.moveToFirst()){
				int bookIndex = cursor.getColumnIndex(FIELD_BOOK_ID);
				int cha = cursor.getColumnIndex(FIELD_CHAPTER);
				int content = cursor.getColumnIndex(FIELD_CONTENT);
				do{
					if(cursor.getInt(bookIndex) == bookId){
						if(cursor.getInt(cha) == chapter){
							poems.add(new PoemStruct(cursor.getString(content))
												.setBookId(bookId)
												.setChapter(chapter));
						}
					}
				}while(cursor.moveToNext());
			}
			</font></b>
			
Путь для получения списка книг с главами для чтения на каждый день
	<b>ua.maker.gbible.provider.RfEDContent</b>
	
Пример:
	Получить Cursor с полной инфой
	<b>content://ua.maker.gbible.provider.RfEDContent/</b>
	
	Код получения инфы из курсора лежит в проекте по пути ua.maker.gbible.utils.DataBase метод и парсер к нему в конце класса <b>public List<ItemReadDay> getListReadForEveryDay(){</b>
	