library(tidyverse)

input_file <- 'results/texts.csv'
df <- read_csv(input_file)

names <- names(df)
df_freq <- as.data.frame(table(df[[3]], useNA="ifany")) %>% 
  rename(value = Var1) %>% mutate(field = names[3])
for (i in seq_along(df)) {
  if (!i %in% c(1, 2, 3)) {
    t <- as.data.frame(table(df[[i]], useNA="ifany"))
    print(t)
    print(t)
    if (nrow(t) > 0) {
      t <- t %>% rename(value = Var1) %>% mutate(field = names[i])
      df_freq <- df_freq %>% union(t)
    } else {
      print(i)
    }
  }
}

df_freq <- df_freq %>% 
  rename(frequency = Freq) %>% 
  select(field, value, frequency) %>% 
  arrange(field)
write_csv(df_freq, 'results/texts-frequency.csv')

df_freq %>% 
  group_by(field) %>% 
  count() %>% 
  rename(number_of_values = n) %>% 
  write_csv('results/texts-variability.csv')

