require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /
    state: Welcome
        q: * *start
        audio: https://cdn.alto-ai.ru/animals/bot_audio/hello_prompt.wav
        audio: https://cdn.alto-ai.ru/animals/bot_audio/hello_part2_prompt.wav
        audio: https://cdn.alto-ai.ru/animals/bot_audio/what_hapenned.wav
        #a: Здравствуйте. Вас приветствует искусственный интеллект Валерия. Я вам помогу верно составиить заявление о плохом обращении с животными и помогу направить его в соответствующий орган. Для этого вам нужно ответить на несколько вопросов.
        #a: Опишите кратко, что произошло?
        go!: What_happen
    
        state: What_happen
            state: Steal
                intent: /украли
                a: Был похищен питомец, правильно?
    
                state: No
                    intent: /нет
                    a: Тогда попробуйте описать случай более кратко, например, украли собаку или нанесли повреждения кошке.
                    go!: /Welcome/What_happen
                
                state: Yes
                    intent: /да
                    script: $session.reason = "украли животное"
                    #go!: /Steal
            
            state: Distruct
                intent: /повредили
                a: Животное было ранено, правильно
    
                state: No
                    intent: /нет
                    a: Тогда попробуйте описать случай более кратко, например, украли собаку или нанесли повреждения кошке.
                    go!: /Welcome/What_happen
                
                state: Yes
                    intent: /да
                    script: $session.reason = "ранили животное"
                    go!: /Distruct
    
            state: Poison
                intent: /яд
                a: Животное было отравлено ядом, правильно?
    
                state: No
                    intent: /нет
                    a: Тогда попробуйте описать случай более кратко, например, украли собаку или нанесли повреждения кошке.
                    go!: /Welcome/What_happen
                
                state: Yes
                    intent: /да
                    script: $session.reason = "отравили животное"
                    go!: /Distruct
                    
            state: NoMatch
                event: noMatch
                a: Извините, я вас не понял вас. Попробуйте переформулировать.
                go!: /Welcome/What_happen
                
    state: Distruct
        #a: Чем было ранено животное?
        audio: https://cdn.alto-ai.ru/animals/bot_audio/what_weapon_prompt.wav

        state: Gun
            intent: /огнестрельное_оружие
            #a: Животное ранили из огнестрельного оружия?
            audio: https://cdn.alto-ai.ru/animals/bot_audio/gun_prompt.wav
            state: No
                intent: /нет
                a: Извините, тогда не поняла.
                go!: /Distruct
            
            state: Yes
                intent: /да
                script: $session.weapon = "пистолет"
                go!: /In_town
                    
        state: Knife
            intent: /нож
            go!: /In_town
            a: Животное ранили ножом?
            state: No
                intent: /нет
                a: Извините, тогда не поняла.
                go!: /Distruct
            
            state: Yes
                intent: /да
                script: $session.weapon = "нож"
                go!: /In_town
                
        state: Poison
            intent: /яд
            go!: /In_town
            a: Животное отравили ядом?
            state: No
                intent: /нет
                a: Извините, тогда не поняла.
                go!: /Distruct
            
            state: Yes
                intent: /да
                script: $session.weapon = "яд"
                go!: /In_town

    state: In_town
        #a: Это произошло в черте города?
        audio: https://cdn.alto-ai.ru/animals/bot_audio/hunter_place_prompt.wav

        state: No
            intent: /нет
            script: $session.in_town = "false"
            go!: /Children
        
        state: Yes
            intent: /да
            script: $session.in_town = "true"
            go!: /Children

    state: Children
        audio: https://cdn.alto-ai.ru/animals/bot_audio/children_prompt.wav
        #a: Свидедетелями данной ситуации были дети?

        state: No
            intent: /нет
            script: $session.children = "false"
            go!: /Home_dog
        
        state: Yes
            intent: /да
            script: $session.children = "true"
            go!: /Home_dog

    state: Home_dog
        a: Было ли это домашнее животное?

        state: No
            intent: /нет
            script: $session.home_animal = "false"
            go!: /Big_dog
        
        state: Yes
            intent: /да
            script: $session.home_animal = "true"
            go!: /Big_dog
    
    state: Big_dog
        a: Была ли это породистая собака с документами?

        state: No
            intent: /нет
            script: $session.has_docs = "false"
            go!: /Select_town
        
        state: Yes
            intent: /да
            script: $session.has_docs = "true"
            go!: /Select_town

    state: Select_town
        a: Назовиите город, в котором вы находитесь
        
        state: Recognition
            q: *
            script: $session.town = $request.query
            go!: /Free_text

    state: Free_text
        #a: Опишите в свободной форме, что произошло?
        audio: https://cdn.alto-ai.ru/animals/bot_audio/free_text_prompt.wav
        
        state: Recognition
            q: *
            script: $session.free_text = $request.query
            go!: /The_End

    state: The_End
        a: Спасибо, в ближайшее время мы вам пришлем файл
        script:
            var url = "https://cdn.alto-ai.ru/animals/handler.php";
            var options = {
                dataType: "json",
                headers: {
                    "Content-Type": "application/json"
                },
                body: {
                    "reason": $session.reason,
                    "weapon": $session.weapon,
                    "in_town": $session.in_town,
                    "children": $session.children,
                    "home_animal": $session.home_animal,
                    "has_docs": $session.has_docs,
                    "town": $session.town,
                    "free_text": $session.free_text,
                    "phone": $dialer.getCaller()
                }
            };
            var response = $http.post(url, options);
            
        

    state: NoMatch
        event!: noMatch
        a: Извините, я вас не понял. Попробуйте переформулировать.
        