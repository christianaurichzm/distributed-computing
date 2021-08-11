#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <omp.h>
#include <sys/time.h>
#include <ctype.h>

int qtPalavras = 8;
char palavras[8][14] = {"mundo", "modelo", "saber", "esporte", "financeiro", "empresa", "porta", "mercado"};

void num_ocorrencias(char *linha, char *palavra)
{
    int seq = 0;
    int cont = 0;

    for (int i = 0; i < strlen(linha); i++)
    {
        if (tolower(palavra[seq]) == tolower(linha[i]))
            seq++;
        else
            seq = 0;
        if (strlen(palavra) == seq)
        {
            cont++;
            seq = 0;
        }
    }

    printf("A palavra %s aparece %d vezes no livro.\n", palavra, cont);
}

int main()
{
    struct timeval t1, t2;
    FILE *file = fopen("livroLeonard.txt", "r"); // Trocar pelo txt do livro desejado
    char *linha;
    long tamanho;

    if (file != NULL)
    {
        fseek(file, 0, SEEK_END);        // move cursor para fim do livro para podermos pegar o tamanho do arquivo
        tamanho = ftell(file);           // recebe tamanho do arquivo em bytes
        fseek(file, 0, SEEK_SET);        // move cursor novamente para o inicio do arquivo
        linha = (char *)malloc(tamanho); // aloca espaço em memoria para linha
        fread(linha, 1, tamanho, file);  // leitura da estrutura de armazenamento da linha
        fclose(file);
    }
    else
    {
        printf("Erro na leitura do arquivo de texto.");
    }

    gettimeofday(&t1, NULL);

    for (int i = 0; i < qtPalavras; i++)
        num_ocorrencias(linha, palavras[i]); // invocando o contador de ocorrencias das palavras chave

    gettimeofday(&t2, NULL);

    double t_total = (t2.tv_sec - t1.tv_sec) + ((t2.tv_usec - t1.tv_usec) / 1000000.0);

    printf("tempo total = %f\n", t_total);
}
