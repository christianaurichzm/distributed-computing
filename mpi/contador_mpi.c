#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <string.h>
#include <mpi.h>
#include <ctype.h>

#define QT_PALAVRAS 8

int total_ocorrencias[QT_PALAVRAS];
char palavras[QT_PALAVRAS][14] = {"mundo", "modelo", "saber", "esporte", "financeiro", "empresa", "porta", "mercado"};

int num_ocorrencias(char *linha, char *palavra, int id_processo, int numero_processos)
{
    int tamanho_buffer_processo = strlen(linha) / (numero_processos - 1);
    int cursor = 0;
    int ocorrencias = 0;

    int posicao_inicial_processo = tamanho_buffer_processo * (id_processo - 1);
    int posicao_final_processo = posicao_inicial_processo + tamanho_buffer_processo;

    for (int i = posicao_inicial_processo; i < posicao_final_processo; i++)
    {
        if (tolower(palavra[cursor]) == tolower(linha[i]))
            cursor++;
        else
            cursor = 0;
        if (cursor == strlen(palavra))
        {
            ocorrencias++;
            cursor = 0;
        }
    }
    return ocorrencias;
}

int main(int argc, char *argv[])
{
    FILE *file = fopen("livroLeonard.txt", "r"); // Trocar pelo txt do livro desejado
    struct timeval t1, t2;
    char *linha;
    long tamanho;
    int i, j, tag = 1, rank, size;
    MPI_Status status;

    if (file != NULL)
    {
        fseek(file, 0, SEEK_END);
        tamanho = ftell(file);
        fseek(file, 0, SEEK_SET);
        linha = (char *)malloc(tamanho);
        fread(linha, 1, tamanho, file);
        fclose(file);
    }
    else
    {
        printf("Erro na leitura do arquivo de texto. \n");
    }

    MPI_Init(&argc, &argv);

    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    int ocorrencias_processo[QT_PALAVRAS];

    if (size < 2)
    {
        fprintf(stderr, "Deve ter mais de 1 processo para esse programa. \n");
        MPI_Abort(MPI_COMM_WORLD, 1);
    }

    if (rank == 0)
    {
        gettimeofday(&t1, NULL);

        for (i = 1; i < size; i++)
        {
            MPI_Recv(ocorrencias_processo, QT_PALAVRAS, MPI_INT, i, tag, MPI_COMM_WORLD, &status);

            for (j = 0; j < QT_PALAVRAS; j++)
            {
                total_ocorrencias[j] = total_ocorrencias[j] + ocorrencias_processo[j];
            }
        }

        for (i = 0; i < QT_PALAVRAS; i++)
        {
            printf("%s aparece %d vezes no livro.\n", palavras[i], total_ocorrencias[i]);
        }

        gettimeofday(&t2, NULL);
        double t_total = (t2.tv_sec - t1.tv_sec) + ((t2.tv_usec - t1.tv_usec) / 1000000.0);
        printf("tempo total = %f\n", t_total);
    }

    else
    {
        for (i = 0; i < QT_PALAVRAS; i++)
        {
            ocorrencias_processo[i] = num_ocorrencias(linha, palavras[i], rank, size);
        }

        MPI_Send(ocorrencias_processo, QT_PALAVRAS, MPI_INT, 0, tag, MPI_COMM_WORLD);
    }

    MPI_Finalize();

    return 0;
}
