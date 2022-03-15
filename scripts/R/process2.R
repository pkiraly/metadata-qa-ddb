library(tidyverse)

args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  stop("At least one argument must be supplied (input file).n", call.=FALSE)
} else if (length(args) == 1) {
  # default output file
  dir <- args[1]
}

df <- read_csv(paste0(dir, '/raw.csv'))

n <- df %>% count() %>% unlist(use.names = FALSE)
cat(n, file=paste0(dir, '/count'))

names <- names(df)
# df_freq <- as.data.frame(table(df[[3]], useNA="ifany")) %>% 
#   rename(value = Var1) %>% mutate(field = names[3])
df_freq <- NULL 
for (i in seq_along(df)) {
  if (!i %in% c(1)) {
    t <- as.data.frame(table(df[[i]], useNA="ifany"))
    if (nrow(t) > 0) {
      t <- t %>% rename(value = Var1) %>% mutate(field = names[i])
      if (is.null(df_freq)) {
        df_freq <- t
      } else {
        df_freq <- df_freq %>% union(t)
      }
    } else {
      print(i)
      print(t)
    }
  }
}


df_freq <- df_freq %>% 
  rename(frequency = Freq) %>% 
  select(field, value, frequency) %>% 
  arrange(field)
write_csv(df_freq, paste0(dir, '/frequency.csv'))

df_freq %>% 
  group_by(field) %>% 
  count() %>% 
  rename(number_of_values = n) %>% 
  write_csv(paste0(dir, '/variability.csv'))
