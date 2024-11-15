library(tidyverse)

args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  stop("At least one argument must be supplied (input file).n", call.=FALSE)
} else if (length(args) == 1) {
  # default output file
  dir <- args[1]
}

calculateFrequency <- function(df) {
  names <- names(df)
  df_freq <- NULL 
  for (i in seq_along(df)) {
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
  df_freq <- df_freq %>% 
    rename(frequency = Freq) %>% 
    select(field, value, frequency) %>% 
    arrange(field)
  return(df_freq)
}

join_df <- function(A, B) {
  if (is.null(A)) {
    A <- B
  } else {
    A <- A %>% union(B)
  }
  
  return(A)
}

df <- read_csv(paste0(dir, '/all-issues.csv'))
print(str(df))

n <- df %>% count() %>% unlist(use.names = FALSE)
cat(n, file=paste0(dir, '/count'))

view(df)
config <- list()
fields <- c('metadata_schema', 'set_id', 'provider_id') #, 'file')
for (field in fields) {
  values <- c(NA, df %>% select(all_of(field)) %>% distinct() %>% unlist(use.names = FALSE))
  config[[field]] <- values
}

df_perm <- expand.grid(config[['metadata_schema']], config[['set_id']], config[['provider_id']])
names(df_perm) <- fields

df_perm <- df_perm %>% mutate(count = 0)
head(df_perm)

df_freq_all <- NULL
df_var_all <- NULL
for (i in 1:nrow(df_perm)) {
  # df_perm
  keys <- c()
  values <- c()
  for (j in 1:length(fields)) {
    field <- fields[j]
    value <- df_perm[i, j]
    # print(value)
    if (!is.na(value)) {
      keys <- c(keys, field)
      values <- c(values, as.character(value))
    }
  }

  if (length(keys) == 0) {
    criteria <- "all"
    df_filtered <- df
  } else if (length(keys) == 1) {
    key1 <- rlang::sym(keys[1])
    criteria <- sprintf("%s == %s", keys[1], values[1])
    df_filtered <- df %>% filter(!!key1 == values[1])
  } else if (length(keys) == 2) {
    key1 <- rlang::sym(keys[1])
    key2 <- rlang::sym(keys[2])
    criteria <- sprintf("%s == %s, %s == %s", keys[1], values[1], keys[2], values[2])
    df_filtered <- df %>% filter(!!key1 == values[1] & !!key2 == values[2])
  } else if (length(keys) == 3) {
    key1 <- rlang::sym(keys[1])
    key2 <- rlang::sym(keys[2])
    key3 <- rlang::sym(keys[3])
    criteria <- sprintf("%s == %s, %s == %s, %s == %s", keys[1], values[1], keys[2], values[2], keys[3], values[3])
    df_filtered <- df %>% filter(!!key1 == values[1] & !!key2 == values[2] & !!key3 == values[3])
  } else {
    df_filtered <- NULL
    criteria <- "NULL"
  }

  if (!is.null(df_filtered)) {
    .count <- nrow(df_filtered)
    if (.count > 0) {
      current_schema <- df_filtered[1, 1]  %>% unlist(use.names = FALSE)
      isEdm <- ifelse(current_schema == 'DDB-EDM', TRUE, FALSE)
      df_perm <- df_perm %>% mutate(count = ifelse(row_number() == i, .count, count))
      
      # print(df_perm[i, ])
      # print(.count)
      df_filtered <- df_filtered %>% 
        select(-c("metadata_schema", "set_id", "provider_id", "file", "recordId", "providerid"))
      df_freq <- calculateFrequency(df_filtered)
      
      df_var <- df_freq %>% 
        group_by(field) %>% 
        count() %>% 
        rename(number_of_values = n)
      
      for (j in 1:length(fields)) {
        field <- rlang::sym(fields[j])
        myvalue <- df_perm[i, j]
        # print(sprintf('%s %s', fields[j], myvalue))
        df_freq <- df_freq %>% mutate(!!field := myvalue)
        df_var  <- df_var  %>% mutate(!!field := myvalue)
      }
      df_freq_all <- join_df(df_freq_all, df_freq)
      df_var_all <- join_df(df_var_all, df_var)
    }
  }
}
write_csv(df_freq_all, paste0(dir, '/frequency.csv'))
write_csv(df_var_all,  paste0(dir, '/variability.csv'))
write_csv(df_perm,  paste0(dir, '/count.csv'))
